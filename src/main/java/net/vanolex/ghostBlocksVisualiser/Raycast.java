package net.vanolex.ghostBlocksVisualiser;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.Entity;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public class Raycast {

    @Nullable
    public static LivingEntity raycast(Player p) {
        Location eyeLoc = p.getEyeLocation(); // eye level
        Vector dir = p.getLocation().getDirection().normalize();

        double closestDist = voxelTravelToBlock(eyeLoc, dir, 3.0, p.getWorld());

        LivingEntity hitEntity = null;
        for (LivingEntity entity : p.getWorld().getEntitiesByClass(LivingEntity.class)) {
            Location l = entity.getLocation();
            if (p == entity) continue;

            AxisAlignedBB aabb = ((CraftEntity) entity).getHandle().getBoundingBox();

            if (aabb.d < eyeLoc.getX() - closestDist || aabb.a > eyeLoc.getX() + closestDist) continue;
            if (aabb.e < eyeLoc.getY() - closestDist || aabb.b > eyeLoc.getY() + closestDist) continue;
            if (aabb.f < eyeLoc.getZ() - closestDist || aabb.c > eyeLoc.getZ() + closestDist) continue;

            Pair<Double, Double> xT = Utils.calcSlab(aabb.a, aabb.d, eyeLoc.getX(), dir.getX());
            if (xT.getLeft() > closestDist) continue;
            if (xT.getRight() < 0) continue;

            Pair<Double, Double> yT = Utils.calcSlab(aabb.b, aabb.e, eyeLoc.getY(), dir.getY());
            if (yT.getLeft() > closestDist) continue;
            if (yT.getRight() < 0) continue;

            Pair<Double, Double> zT = Utils.calcSlab(aabb.c, aabb.f, eyeLoc.getZ(), dir.getZ());
            if (zT.getLeft() > closestDist) continue;
            if (zT.getRight() < 0) continue;

            double Tenter = Math.max(Math.max(xT.getLeft(), yT.getLeft()), zT.getLeft());
            double Texit = Math.min(Math.min(xT.getRight(), yT.getRight()), zT.getRight());
            if (Tenter > Texit) continue; // no need for additional checks because each pair was checked individually

            hitEntity = entity;
            closestDist = Tenter;
        }
        return hitEntity;
    }

    public static double voxelTravelToBlock(Location start, Vector direction, double maxDistance, World world) {
        int x = start.getBlockX();
        int y = start.getBlockY();
        int z = start.getBlockZ();

        double dx = direction.getX();
        double dy = direction.getY();
        double dz = direction.getZ();

        int stepX = dx > 0 ? 1 : (dx < 0 ? -1 : 0);
        int stepY = dy > 0 ? 1 : (dy < 0 ? -1 : 0);
        int stepZ = dz > 0 ? 1 : (dz < 0 ? -1 : 0);

        double tMaxX = stepX != 0 ? ((stepX > 0 ? (x + 1) : x) - start.getX()) / dx : Double.POSITIVE_INFINITY;
        double tMaxY = stepY != 0 ? ((stepY > 0 ? (y + 1) : y) - start.getY()) / dy : Double.POSITIVE_INFINITY;
        double tMaxZ = stepZ != 0 ? ((stepZ > 0 ? (z + 1) : z) - start.getZ()) / dz : Double.POSITIVE_INFINITY;

        double tDeltaX = stepX != 0 ? 1.0 / Math.abs(dx) : Double.POSITIVE_INFINITY;
        double tDeltaY = stepY != 0 ? 1.0 / Math.abs(dy) : Double.POSITIVE_INFINITY;
        double tDeltaZ = stepZ != 0 ? 1.0 / Math.abs(dz) : Double.POSITIVE_INFINITY;

        double traveled = 0.0;

        while (traveled <= maxDistance) {
            Material block = world.getBlockAt(x, y, z).getType();
            if (block.isSolid()) {
                return traveled;
            }

            if (tMaxX < tMaxY && tMaxX < tMaxZ) {
                x += stepX;
                traveled = tMaxX;
                tMaxX += tDeltaX;
            } else if (tMaxY < tMaxZ) {
                y += stepY;
                traveled = tMaxY;
                tMaxY += tDeltaY;
            } else {
                z += stepZ;
                traveled = tMaxZ;
                tMaxZ += tDeltaZ;
            }
        }

        return maxDistance; // no block hit
    }

}
