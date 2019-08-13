package com.mineaurion.api.commands;

import com.mineaurion.aurionworld.AurionWorld;
import net.minecraft.command.ICommandSender;

import java.util.HashMap;
import java.util.List;

public class SubCommand extends Command {

    public SubCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    protected void process(ICommandSender sender, String[] args) {

    }
}
