package com.mineaurion.aurionworld.commands.world.subcommands;

import com.mineaurion.aurionworld.core.Log;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.world.AWorld;
import com.mineaurion.aurionworld.world.AWorldManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.*;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;


public class WorldCreateCommand extends SubCommand {
    public WorldCreateCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        String name = "Ashk-1";
        String provider = AWorldManager.PROVIDER_NORMAL;
        String worldType = WorldType.DEFAULT.getWorldTypeName();
        long seed;

        AWorld world = new AWorld(name, provider, worldType);

    }
}
