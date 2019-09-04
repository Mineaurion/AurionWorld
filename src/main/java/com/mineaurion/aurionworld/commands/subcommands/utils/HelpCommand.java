package com.mineaurion.aurionworld.commands.subcommands.utils;

import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import net.minecraft.command.ICommandSender;

public class HelpCommand extends SubCommand {
    public HelpCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        ChatHandler.sendMessage(sender, "Help Message!");
    }
}
