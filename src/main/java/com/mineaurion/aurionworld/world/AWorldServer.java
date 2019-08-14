package com.mineaurion.aurionworld.world;

import com.mineaurion.aurionworld.core.misc.SimpleTeleporter;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;

public class AWorldServer extends WorldServer
{

    private SimpleTeleporter worldTeleporter;

    public AWorldServer(MinecraftServer mcServer, ISaveHandler saveHandler, String worldname, int dimensionId, WorldSettings worldSettings,
                                 WorldServer worldServer, Profiler profiler, AWorld world)
    {
        super(mcServer, saveHandler, worldname, dimensionId, worldSettings, profiler);
        this.mapStorage = worldServer.mapStorage;
        this.worldScoreboard = worldServer.getScoreboard();
        this.worldTeleporter = new SimpleTeleporter(this);
    }

    @Override
    public Teleporter getDefaultTeleporter()
    {
        return this.worldTeleporter;
    }

    @Override
    protected void saveLevel() throws MinecraftException
    {
        this.perWorldStorage.saveAllData();
        this.saveHandler.saveWorldInfo(this.worldInfo);
    }

}
