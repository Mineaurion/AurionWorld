package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.core.misc.WorldUtil;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class SetSpawnCommand extends SubCommand {
    public SetSpawnCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length != 0) {
            return;
        }

        if (!WorldUtil.canDoOwnerAction(sender)) {
            AurionWorld.sendMessage(sender, "You are not allowed to do that!");
            return;
        }

        int dimId = ((EntityPlayer)sender).dimension;
        AWorld world = AurionWorld.getWorldManager().getWorld(dimId);
        world.setSpawn(
                Double.valueOf(((EntityPlayer)sender).posX).intValue(),
                Double.valueOf(((EntityPlayer)sender).posY).intValue(),
                Double.valueOf(((EntityPlayer)sender).posZ).intValue()
        );

        AurionWorld.sendMessage(sender, "Your world spawn is now here!");
        world.save();
    }
}
