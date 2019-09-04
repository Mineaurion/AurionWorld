package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;

import java.util.Optional;

public class UnloadCommand extends SubCommand {
    public UnloadCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            return;
        }

        Optional<AWorld> world = AurionWorld.getWorldManager().getWorld(args[0]);
        if (!world.isPresent()) {
            ChatHandler.sendMessage(sender, "This world doesn't exist!");
            return;
        }
        if (!AurionWorld.isOp(sender)) {
            ChatHandler.sendMessage(sender, "You are not allowed to do that!");
            return;
        }
        if (!world.get().isLoaded()) {
            ChatHandler.sendMessage(sender, "This world is already unloaded!");
            return;
        }
        AurionWorld.getWorldManager().unloadWorld(world.get(), false);
        ChatHandler.sendMessage(sender, "World " + world.get().getName() + " has been sucessfully unloaded!");
    }
}
