package com.mineaurion.aurionworld.core.commands;

import net.minecraft.command.ICommandSender;

public abstract class ACommandSub extends ACommand {

    public ACommandSub(String id, ACommand parent) {
        super(id, parent);
    }

    @Override
    protected void process(ICommandSender sender, String[] args) {

    }
}
