package net.vanolex.ghostBlocksVisualiser;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.plugin.java.JavaPlugin;
public final class GhostBlocksVisualiser extends JavaPlugin {

    public static GhostBlocksVisualiser plugin;

    @Override
    public void onEnable() {
        plugin = this;

        ProtocolLibrary.getProtocolManager().addPacketListener(new StandPacketListener());

        getServer().getPluginManager().registerEvents(new WorldUnloadListener(), this);

        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }
}
