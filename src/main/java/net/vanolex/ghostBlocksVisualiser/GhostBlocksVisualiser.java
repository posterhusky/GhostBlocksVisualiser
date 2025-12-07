package net.vanolex.ghostBlocksVisualiser;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;

public final class GhostBlocksVisualiser extends JavaPlugin {

    @Override
    public void onEnable() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new StandPacketListener(this));

        getServer().getPluginManager().registerEvents(new WorldUnloadListener(), this);

        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }
}
