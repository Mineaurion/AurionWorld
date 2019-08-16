package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class TeleportCommand extends SubCommand {
    public TeleportCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        AurionWorld.sendMessage(sender, args[0]);
        AurionWorld.getWorldManager().getWorld(args[0]).teleport((EntityPlayerMP) sender, true);
    }
}
