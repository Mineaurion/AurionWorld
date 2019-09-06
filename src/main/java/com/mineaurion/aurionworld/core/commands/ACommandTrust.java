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
            throw new AUsageException((args.length > 2) ? AUsageException.TOO_MANY : AUsageException.NOT_ENOUGH);
        }

        // Check if AWorld not exist
        if (!givenWorld.isPresent())
            throw new ACommandException(
                    (args.length == 2)
                            ? ACommandException.NOT_ALLOWED
                            : ACommandException.WORLD_NOT_EXIST
            );

        world = givenWorld.get();
        // Check if sender respect condition to execute the command
        if (!world.canDoOwnerAction(sender, beInside))
            throw new ACommandException(ACommandException.NOT_ALLOWED);

        Optional<UUID> givenTargetUuid = AurionWorld.getPlayerUuid(targetName);
        // Check if target exist
        if (!givenTargetUuid.isPresent())
            throw new ACommandException(String.format(ACommandException.PLAYER_NOT_EXIST, targetName));

        targetUuid = givenTargetUuid.get();

        // Check if target is not the creator
        if (world.isUniqueOwner(targetUuid))
            throw new ACommandException(String.format(ACommandException.PLAYER_IS_CREATE, targetName));

        // Check if sender try to manage him self..
        if (senderUuid.equals(targetUuid))
            throw new ACommandException(ACommandException.PLAYER_CANT_MANAGE_HIMSELF);

        return true;
    }
}

