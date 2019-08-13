package com.mineaurion.aurionworld.commands;

import com.mineaurion.api.Log;
import com.mineaurion.api.commands.Command;
import com.mineaurion.api.commands.SubCommand;
import com.mineaurion.aurionworld.AurionWorld;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class HelpCommand extends SubCommand {
    public HelpCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        AurionWorld.sendMessage(sender, "Help Message!");
    }
}
