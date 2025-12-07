package net.vanolex.ghostBlocksVisualiser;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldUnloadListener implements Listener {

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        WorldManager.unloadWorld(event.getWorld());
    }

}
