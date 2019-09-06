package com.mineaurion.aurionworld.commands.subcommands.utils;

import com.mineaurion.aurionworld.core.commands.ACommand;
import com.mineaurion.aurionworld.core.commands.ACommandSub;
import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import net.minecraft.command.ICommandSender;

public class ReloadCommand extends ACommandSub {
    public ReloadCommand(String id, ACommand parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        AurionWorld.reload();
        ChatHandler.chatConfirmation(sender, "Plugin AurionWorld has been reloaded!");
    }
}
