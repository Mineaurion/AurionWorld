package com.mineaurion.aurionworld.commands;

import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.AurionWorld;
import net.minecraft.command.ICommandSender;

public class HelpCommand extends SubCommand {
    public HelpCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        AurionWorld.sendMessage(sender, "Help Message!");
    }
}
