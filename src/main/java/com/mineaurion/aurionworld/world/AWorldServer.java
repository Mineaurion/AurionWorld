package com.mineaurion.aurionworld.world;

import com.mineaurion.aurionworld.core.misc.output.Log;
import com.mineaurion.aurionworld.core.misc.teleporter.TeleportHelper;
import com.mineaurion.aurionworld.core.misc.teleporter.TeleportHelper.SimpleTeleporter;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
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
    public net.minecraft.world.Teleporter getDefaultTeleporter()
    {
        return this.worldTeleporter;
    }

    @Override
    protected void saveLevel() throws MinecraftException
    {
        this.perWorldStorage.saveAllData();
        this.saveHandler.saveWorldInfo(this.worldInfo);
    }

    public void save() {
        try {
            saveLevel();
        } catch (MinecraftException e) {
            e.printStackTrace();
        }
    }

}
