package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.ACommand;
import com.mineaurion.aurionworld.core.commands.ACommandException;
import com.mineaurion.aurionworld.core.commands.ACommandSub;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;

import java.util.Optional;

public class LoadCommand extends ACommandSub {
    public LoadCommand(String id, ACommand parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            return;
        }

        Optional<AWorld> world = AurionWorld.getWorldManager().getWorld(args[0]);
        if (!world.isPresent())
            throw new ACommandException("This world doesn't exist!");

        if (world.get().isLoaded())
            throw new ACommandException("This world is already loaded!");

        AurionWorld.getWorldManager().loadWorld(world.get(), false);

        if (world.get().isLoaded())
            ChatHandler.chatConfirmation(sender, "World " + world.get().getName() + " start loading");
        else
            throw new ACommandException("World " + world.get().getName() + " can't be loaded");
    }
}
