package com.mineaurion.aurionworld.commands;

import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.AurionWorld;
import net.minecraft.command.ICommandSender;

public class ReloadCommand extends SubCommand {
    public ReloadCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        AurionWorld.reload();
        AurionWorld.sendMessage(sender, "Plugin reloaded!");
    }
}
