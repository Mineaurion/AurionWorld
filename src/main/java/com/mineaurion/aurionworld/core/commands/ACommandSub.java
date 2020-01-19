package com.mineaurion.aurionworld.core.commands;

import com.mineaurion.aurionworld.world.AWorldException;
import net.minecraft.command.ICommandSender;

public abstract class ACommandSub extends ACommand {

    public ACommandSub(String id, ACommand parent) {
        super(id, parent);
    }

    public void preProcess(ICommandSender sender, String[] args) throws ACommandException, AUsageException, AWorldException {
        if (canCommandSenderUseCommand(sender))
            processCommand(sender, args);
        else throw new ACommandException(ACommandException.NOT_PERMISSION);
    }
}
