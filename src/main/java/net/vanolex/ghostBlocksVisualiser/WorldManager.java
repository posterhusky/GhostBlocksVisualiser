package net.vanolex.ghostBlocksVisualiser;

import com.avaje.ebean.validation.NotNull;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class WorldManager {
    private static final Map<World, WorldManager> worldManagers = new HashMap<>();

    public static WorldManager getOrCreateWorldManager(World w) {
        if (worldManagers.containsKey(w)) {
            return worldManagers.get(w);
        }
        return new WorldManager(w);
    }

    public static void unloadWorld(World w) {
        worldManagers.remove(w);
    }

    Map<EntityArmorStand, EntityFallingBlock> ghostBlocks = new HashMap<>();
    WorldServer worldServer;

    public WorldManager(World w) {
        worldManagers.put(w, this);
        worldServer = ((CraftWorld) w).getHandle();
    }

    @Nullable
    public EntityFallingBlock getGhostBlockOrNull(int standId) {
        if (ghostBlocks.containsKey(standId)) {
            return ghostBlocks.get(standId);
        }
        return null;
    }

    @NotNull
    public EntityFallingBlock getOrCreateGhostBlock(EntityArmorStand stand) {
        if (ghostBlocks.containsKey(stand)) {
            return ghostBlocks.get(stand);
        }

        EntityFallingBlock ghost = new EntityFallingBlock(worldServer);
        ghost.setLocation(stand.locX, stand.locY, stand.locZ, 0f, 0f);
        ghost.ticksLived = 1;
        ghost.dropItem = false;

        ghostBlocks.put(stand, ghost);
        return ghost;
    }
}
