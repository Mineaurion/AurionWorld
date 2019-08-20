package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Optional;

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
        Optional<AWorld> world = AurionWorld.getWorldManager().getWorld(args[0]);
        if (!world.isPresent()) {
            AurionWorld.sendMessage(sender, "This world doesn't exist!");
            return;
        }
        if (!world.get().isLoaded()) {
            AurionWorld.sendMessage(sender, "This world isn't loaded!!");
            return;
        }
        world.get().teleport((EntityPlayerMP) sender, true);
    }
}
