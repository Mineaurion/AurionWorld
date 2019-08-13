package com.mineaurion.aurionworld.commands.world.subcommands;

import com.mineaurion.api.commands.Command;
import com.mineaurion.api.commands.SubCommand;
import com.mineaurion.aurionworld.AurionWorld;
import net.minecraft.command.ICommandSender;

public class WorldHelpCommand extends SubCommand {
    public WorldHelpCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        AurionWorld.sendMessage(sender, "Help Message!");
    }
}
