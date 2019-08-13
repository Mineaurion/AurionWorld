package com.mineaurion.aurionworld.commands.world;

import com.mineaurion.api.commands.Command;
import com.mineaurion.api.commands.SubCommand;
import com.mineaurion.aurionworld.commands.world.subcommands.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraftforge.common.DimensionManager;

public class WorldCommand extends SubCommand {
    public WorldCommand(String id, Command parent) {
        super(id, parent);
        setSubCommand(new WorldCreateCommand("create", this));
        setSubCommand(new WorldDeleteCommand("delete", this));
        setSubCommand(new WorldLoadCommand("load", this));
        setSubCommand(new WorldUnloadCommand("unload", this));
        setSubCommand(new WorldHelpCommand("help", this));
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        getSubCommandById("help").execute(sender, args);
    }
}
