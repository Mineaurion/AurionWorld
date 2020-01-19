package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.ACommand;
import com.mineaurion.aurionworld.core.commands.ACommandException;
import com.mineaurion.aurionworld.core.commands.ACommandSub;
import com.mineaurion.aurionworld.core.commands.AUsageException;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;

import java.util.Optional;

public class DeleteCommand extends ACommandSub {
    public DeleteCommand(String id, ACommand parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length != 1)
            throw new AUsageException();

        Optional<AWorld> world = AurionWorld.getWorldManager().getWorld(args[0]);
        // World doesn't exist
        if (!world.isPresent())
            throw new ACommandException(ACommandException.WORLD_NOT_EXIST);

        String name = world.get().getName();
        AurionWorld.getWorldManager().deleteWorld(world.get());
        ChatHandler.chatConfirmation(sender, "World " + name + " has been succesfully deleted!");
    }
}
