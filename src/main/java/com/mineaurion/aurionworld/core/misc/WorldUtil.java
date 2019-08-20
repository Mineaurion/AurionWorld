package com.mineaurion.aurionworld.core.misc;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.misc.point.WorldPoint;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import scala.tools.cmd.Opt;

import java.util.Optional;

public abstract class WorldUtil {

    /**
     * Checks if the blocks from [x,y,z] to [x,y+h-1,z] are either air or replacable
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param h
     * @return y value
     */
    public static boolean isFree(World world, int x, int y, int z, int h) {
        for (int i = 0; i < h; i++) {
            Block block = world.getBlock(x, y + i, z);
            if (block.getMaterial().isSolid() || block.getMaterial().isLiquid())
                return false;
        }
        return true;
    }

    /**
     * Returns a free spot of height h in the worlds at the coordinates [x,z] near y. If the blocks at [x,y,z] are free,
     * it returns the next location that is on the ground. If the blocks at [x,y,z] are not free, it goes up until it
     * finds a free spot.
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param h
     * @return y value
     */
    public static int placeInWorld(World world, int x, int y, int z, int h) {
        if (y >= 0 && isFree(world, x, y, z, h)) {
            while (isFree(world, x, y - 1, z, h) && y > 0)
                y--;
        } else {
            if (y < 0)
                y = 0;
            y++;
            while (y + h < world.getHeight() && !isFree(world, x, y, z, h))
                y++;
        }
        if (y == 0)
            y = world.getHeight() - h;
        return y;
    }

    /**
     * Returns a free spot of height 2 in the worlds at the coordinates [x,z] near y. If the blocks at [x,y,z] are free,
     * it returns the next location that is on the ground. If the blocks at [x,y,z] are not free, it goes up until it
     * finds a free spot.
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @return y value
     */
    public static int placeInWorld(World world, int x, int y, int z) {
        return placeInWorld(world, x, y, z, 2);
    }

    public static WorldPoint placeInWorld(WorldPoint p) {
        return p.setY(placeInWorld(p.getWorld(), p.getX(), p.getY(), p.getZ(), 2));
    }

    public static void placeInWorld(EntityPlayer player) {
        WorldPoint p = placeInWorld(new WorldPoint(player));
        player.setPositionAndUpdate(p.getX() + 0.5, p.getY(), p.getZ() + 0.5);
    }

    public static Optional<AWorld> whereIsPlayer(EntityPlayerMP player) {
        if (player == null)
            return Optional.empty();
        return AurionWorld.getWorldManager().getWorld(player.dimension);
    }

}

