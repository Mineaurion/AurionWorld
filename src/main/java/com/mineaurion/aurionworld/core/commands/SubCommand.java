package com.mineaurion.aurionworld.core.commands;

import net.minecraft.command.ICommandSender;

public abstract class SubCommand extends Command {

    public SubCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    protected void process(ICommandSender sender, String[] args) {

    }
}
