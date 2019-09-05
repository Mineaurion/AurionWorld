package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Optional;

public class TeleportCommand extends SubCommand {
    public TeleportCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        ChatHandler.sendMessage(sender, args[0]);
        Optional<AWorld> world = AurionWorld.getWorldManager().getWorld(args[0]);
        if (!world.isPresent()) {
            ChatHandler.chatError(sender, "This world doesn't exist!");
            return;
        }
        if (!world.get().isLoaded()) {
            ChatHandler.chatError(sender, "This world isn't loaded!!");
            return;
        }
        world.get().teleport((EntityPlayerMP) sender, true);
        ChatHandler.chatConfirmation(sender, "You have been teleported to the world " + world.get().getName());
    }
}
