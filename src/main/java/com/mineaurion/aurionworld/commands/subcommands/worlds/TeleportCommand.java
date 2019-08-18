package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class TeleportCommand extends SubCommand {
    public TeleportCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        AurionWorld.sendMessage(sender, args[0]);
        AWorld world = AurionWorld.getWorldManager().getWorld(args[0]);
        if (world == null) {
            AurionWorld.sendMessage(sender, "This world doesn't exist!");
            return;
        }
        if (!world.isLoaded()) {
            AurionWorld.sendMessage(sender, "This world isn't loaded!!");
            return;
        }
        world.teleport((EntityPlayerMP) sender, true);
    }
}
