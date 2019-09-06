package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.ACommand;
import com.mineaurion.aurionworld.core.commands.ACommandException;
import com.mineaurion.aurionworld.core.commands.ACommandSub;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;

import java.util.Optional;

public class UnloadCommand extends ACommandSub {
    public UnloadCommand(String id, ACommand parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        Optional<AWorld> world = AurionWorld.getWorldManager().getWorld(args[0]);
        if (!world.isPresent())
            throw new ACommandException(ACommandException.WORLD_NOT_EXIST);

        if (!world.get().isLoaded())
            throw new ACommandException(ACommandException.WORLD_IS_LOADED);

        AurionWorld.getWorldManager().unloadWorld(world.get(), false);
        ChatHandler.chatConfirmation(sender, "World " + world.get().getName() + " has been sucessfully unloaded!");
    }
}
