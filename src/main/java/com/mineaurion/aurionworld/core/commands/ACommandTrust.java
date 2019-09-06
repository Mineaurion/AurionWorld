package com.mineaurion.aurionworld.core.commands;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.misc.WorldUtil;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Optional;
import java.util.UUID;

public class ACommandTrust extends ACommandSub {
    protected AWorld world;
    protected String targetName;
    protected UUID targetUuid;
    protected boolean beInside;

    public ACommandTrust(String id, ACommand parent) {
        super(id, parent);
    }


    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    protected void process(ICommandSender sender, String[] args) {

    }

    protected boolean checkProcess(ICommandSender sender, String[] args) {
        // ALL COMMAND FORMATS POSSIBLE
        // /aw <TRUST_ACTION> [playerName] ?sender need to be inside owned world
        // /aw <TRUST_ACTION> [worldName] [playerName] ?sender can be outside

        Optional<AWorld> givenWorld;
        UUID senderUuid = ((EntityPlayerMP) sender).getUniqueID();

        // Player is in world ?
        if (args.length == 1) {
            givenWorld = WorldUtil.whereIsPlayer((EntityPlayerMP) sender);
            targetName = args[0];
            beInside = true;
            // Player give a name AWorld
        } else if (args.length == 2) {
            givenWorld = AurionWorld.getWorldManager().getWorld(args[0]);
            targetName = args[1];
            beInside = false;
        } else {
            throw new AUsageException((args.length > 2) ? "Too many params" : "Not enough params");
        }

        // Check if AWorld not exist
        if (!givenWorld.isPresent())
            throw new ACommandException(
                    (args.length == 2)
                            ? "Not allowed to do this in this world!"
                            : "This world doesn't exist"
            );

        world = givenWorld.get();
        // Check if sender respect condition to execute the command
        if (!world.canDoOwnerAction(sender, beInside))
            throw new ACommandException("Not allowed to do this in this world!");

        Optional<UUID> givenTargetUuid = AurionWorld.getPlayerUuid(targetName);
        // Check if target exist
        if (!givenTargetUuid.isPresent())
            throw new ACommandException("This player doesn't exist!");

        targetUuid = givenTargetUuid.get();

        // Check if target is not the creator
        if (world.isUniqueOwner(targetUuid))
            throw new ACommandException("This player is the creator, you can't manage him");

        // Check if sender try to manage him self..
        if (senderUuid.equals(targetUuid))
            throw new ACommandException("You can't manage yourself");

        return true;
    }
}

