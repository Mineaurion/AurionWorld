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

public class LoadCommand extends ACommandSub {
    public LoadCommand(String id, ACommand parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length != 1)
            throw new AUsageException();

        Optional<AWorld> world = AurionWorld.getWorldManager().getWorld(args[0]);
        if (!world.isPresent())
            throw new ACommandException(ACommandException.WORLD_NOT_EXIST);

        if (world.get().isLoaded())
            throw new ACommandException(ACommandException.WORLD_IS_LOADED);

        AurionWorld.getWorldManager().loadWorld(world.get(), false);

        if (world.get().isLoaded())
            ChatHandler.chatConfirmation(sender, "World " + world.get().getName() + " start loading");
        else
            throw new ACommandException(String.format(ACommandException.WORLD_CANT_BE_LOADED, world.get().getName()));
    }
}
