package net.vanolex.ghostBlocksVisualiser;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.plugin.java.JavaPlugin;
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
