package com.mineaurion.aurionworld.core.misc;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class SimpleTeleporter extends Teleporter
{

    public SimpleTeleporter(WorldServer world)
    {
        super(world);
    }

    @Override
    public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float rotationYaw)
    {
        entity.setLocationAndAngles(x, y, z, rotationYaw, entity.rotationPitch);
        return true;
    }

    @Override
    public void removeStalePortalLocations(long totalWorldTime)
    {
        /* do nothing */
    }

    @Override
    public void placeInPortal(Entity entity, double x, double y, double z, float rotationYaw)
    {
        placeInExistingPortal(entity, x, y, z, rotationYaw);
    }

}
