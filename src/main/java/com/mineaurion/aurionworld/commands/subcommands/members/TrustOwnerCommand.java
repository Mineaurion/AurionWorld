package com.mineaurion.aurionworld.commands.subcommands.members;

import com.mineaurion.aurionworld.core.commands.ACommand;
import com.mineaurion.aurionworld.core.commands.ACommandException;
import com.mineaurion.aurionworld.core.commands.ACommandTrust;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.world.AWorldMember;
import net.minecraft.command.ICommandSender;

import java.util.Optional;

public class TrustOwnerCommand extends ACommandTrust {
    public TrustOwnerCommand(String id, ACommand parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (!checkProcess(sender, args)) {
            return;
        }

        Optional<AWorldMember> member = world.getMember(targetUuid);
        if (member.isPresent()) {
            if (member.get().getLevel() == AWorldMember.TRUST_OWNER)
                throw new ACommandException(targetName + " is already trust as owner member");

            member.get().setLevel(AWorldMember.TRUST_OWNER);
            member.get().save();
        } else {
            world.addMember(targetUuid, AWorldMember.TRUST_OWNER);
        }
        ChatHandler.chatConfirmation(sender, targetName + " is now a member owner of " + world.getName() + " world!");
    }
}
