package net.vanolex.ghostBlocksVisualiser;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class StandPacketListener implements PacketListener {

    private GhostBlocksVisualiser plugin;

    public StandPacketListener(GhostBlocksVisualiser plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        Player p = e.getPlayer();
        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
        WorldManager wm = WorldManager.getOrCreateWorldManager(p.getWorld());
        PacketContainer packet = e.getPacket();

        if (e.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
            int standId = packet.getIntegers().read(0);

            List<WrappedWatchableObject> list = packet.getWatchableCollectionModifier().read(0);

            // search if custom name was sent, field idx 2
            WrappedWatchableObject nameObj = null;
            for (WrappedWatchableObject obj : list) {
                if (obj.getIndex() != 2) continue;
                nameObj = obj;
            }
            if (nameObj == null) return;

            Object value = nameObj.getValue();
            if (value == null) return;
            String rawName = value.toString();
            if (rawName.isEmpty()) return;

            EntityArmorStand stand = null;
            for (ArmorStand st : p.getWorld().getEntitiesByClass(ArmorStand.class)) {
                if (st.getEntityId() != standId) continue;
                if (!(st instanceof CraftArmorStand)) continue; // shouldnt happen but just in case
                stand = ((CraftArmorStand) st).getHandle();
                break;
            }
            if (stand == null) return;

            System.out.print("Custom name: " + rawName);
            Integer combinedId = Utils.getCombinedId(rawName);
            if (combinedId == null) return;

            EntityFallingBlock ghostBlock = wm.getOrCreateGhostBlock(standId);
            connection.sendPacket(new PacketPlayOutEntityDestroy(ghostBlock.getId()));
            connection.sendPacket(new PacketPlayOutSpawnEntity(ghostBlock, 70, combinedId));
            connection.sendPacket(new PacketPlayOutAttachEntity(0, ghostBlock, stand));
        } else if (e.getPacketType() == PacketType.Play.Server.ENTITY_DESTROY) {
            int[] ids = packet.getIntegerArrays().read(0);

            for (int id : ids) {
                EntityFallingBlock ghostBlock = wm.getGhostBlockOrNull(id);
                if (ghostBlock == null) continue;
                connection.sendPacket(new PacketPlayOutEntityDestroy(ghostBlock.getId()));
            }
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {}

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return ListeningWhitelist.newBuilder()
                .types(
                    PacketType.Play.Server.ENTITY_METADATA,
                    PacketType.Play.Server.ENTITY_DESTROY
                )
                .build();
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.EMPTY_WHITELIST;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
