package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.ACommand;
import com.mineaurion.aurionworld.core.commands.ACommandException;
import com.mineaurion.aurionworld.core.commands.ACommandSub;
import com.mineaurion.aurionworld.core.commands.AUsageException;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Optional;
import java.util.UUID;

public class TeleportCommand extends ACommandSub {
    public TeleportCommand(String id, ACommand parent) {
        super(id, parent);
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length != 1)
            throw new AUsageException();

        Optional<AWorld> world = AurionWorld.getWorldManager().getWorld(args[0]);
        // Check AWorld by worldName args[0]
        if (!world.isPresent())
            throw new ACommandException(ACommandException.WORLD_NOT_EXIST);
        // World is not loaded
        if (!world.get().isLoaded())
            throw new ACommandException(ACommandException.WORLD_NOT_LOADED);
        // Check player ex
        EntityPlayerMP player = ((EntityPlayerMP) sender);
        // Check permission
        if (!world.get().canDoMemberAction(sender, false))
            throw new ACommandException(ACommandException.NOT_ALLOWED);

        world.get().teleport(player, true);
        ChatHandler.chatConfirmation(sender, "You have been teleported to the world " + world.get().getName());
    }
}
