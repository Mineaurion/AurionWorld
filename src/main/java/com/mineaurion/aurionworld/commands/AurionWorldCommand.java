package com.mineaurion.aurionworld.commands;

import com.mineaurion.api.commands.Command;
import net.minecraft.command.ICommandSender;

public class AurionWorldCommand extends Command {

    public AurionWorldCommand(String name, int requireLevel, String... aliases) {
        super(name, requireLevel, aliases);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
        return false;
    }

    @Override
    public boolean isUsernameIndex(String[] strings, int i) {
        return false;
    }
}
