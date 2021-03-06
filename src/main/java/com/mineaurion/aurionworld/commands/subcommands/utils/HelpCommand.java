package com.mineaurion.aurionworld.commands.subcommands.utils;

import com.mineaurion.aurionworld.core.commands.ACommand;
import com.mineaurion.aurionworld.core.commands.ACommandSub;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import net.minecraft.command.ICommandSender;

public class HelpCommand extends ACommandSub {
    public HelpCommand(String id, ACommand parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        ChatHandler.sendMessage(sender, "Help Message!");
    }
}
