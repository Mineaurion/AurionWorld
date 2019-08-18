package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;

public class UnloadCommand extends SubCommand {
    public UnloadCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            return;
        }

        AWorld world = AurionWorld.getWorldManager().getWorld(args[0]);
        if (world == null) {
            AurionWorld.sendMessage(sender, "This world doesn't exist!");
            return;
        }
        if (!AurionWorld.isOp(sender)) {
            AurionWorld.sendMessage(sender, "You are not allowed to do that!");
            return;
        }
        if (!world.isLoaded()) {
            AurionWorld.sendMessage(sender, "This world is already unloaded!");
            return;
        }
        AurionWorld.getWorldManager().unloadWorld(world);
        AurionWorld.sendMessage(sender, "World " + world.getName() + " start the unloading");
    }
}
