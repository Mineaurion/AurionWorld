package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.core.commands.ACommand;
import com.mineaurion.aurionworld.core.commands.ACommandException;
import com.mineaurion.aurionworld.core.commands.ACommandSub;
import com.mineaurion.aurionworld.core.commands.AUsageException;
import com.mineaurion.aurionworld.core.misc.WorldUtil;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Optional;

public class SetSpawnCommand extends ACommandSub {
    public SetSpawnCommand(String id, ACommand parent) {
        super(id, parent);
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        EntityPlayerMP player = (EntityPlayerMP)sender;

        Optional<AWorld> currentWorld = WorldUtil.whereIsPlayer(player);

        //He is not inside
        if (!currentWorld.isPresent())
            return;

        if (!currentWorld.get().canDoOwnerAction(sender, true))
            throw new ACommandException(ACommandException.NOT_ALLOWED);

        currentWorld.get().setSpawn(
                Double.valueOf(((EntityPlayer)sender).posX).intValue(),
                Double.valueOf(((EntityPlayer)sender).posY).intValue(),
                Double.valueOf(((EntityPlayer)sender).posZ).intValue()
        );

        currentWorld.get().save();
        ChatHandler.chatConfirmation(sender, "Your world spawn is now here!");
    }
}
