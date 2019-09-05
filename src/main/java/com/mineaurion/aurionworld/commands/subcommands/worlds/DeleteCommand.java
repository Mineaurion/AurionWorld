package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.core.misc.WorldUtil;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;

import java.util.Optional;

public class DeleteCommand extends SubCommand {
    public DeleteCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            return;
        }

        Optional<AWorld> world = AurionWorld.getWorldManager().getWorld(args[0]);
        if (!world.isPresent()) {
            ChatHandler.chatError(sender, "This world doesn't exist!");
            return;
        }
        if (!AurionWorld.isOp(sender)) {
            ChatHandler.chatError(sender, "You are not allowed to do that!");
            return;
        }
        String name = world.get().getName();
        AurionWorld.getWorldManager().deleteWorld(world.get());
        ChatHandler.chatConfirmation(sender, "World " + name + " has been succesfully deleted!");
    }
}
