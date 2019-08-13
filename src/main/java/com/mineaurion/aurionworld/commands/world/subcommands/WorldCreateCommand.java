package com.mineaurion.aurionworld.commands.world.subcommands;

import com.mineaurion.api.commands.Command;
import com.mineaurion.api.commands.SubCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;


public class WorldCreateCommand extends SubCommand {
    public WorldCreateCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {

    }
}
