package com.mineaurion.aurionworld.commands.subcommands.members;

import com.mineaurion.aurionworld.core.commands.*;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.world.AWorldMember;
import net.minecraft.command.ICommandSender;

import java.util.Optional;

public class TrustMemberCommand extends ACommandTrust {
    public TrustMemberCommand(String id, ACommand parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (!checkProcess(sender, args)) {
            return;
        }

        Optional<AWorldMember> member = world.getMember(targetUuid);
        if (member.isPresent()) {
            if (member.get().getLevel() == AWorldMember.TRUST_MEMBER)
                throw new ACommandException(String.format(ACommandException.PLAYER_IS_ALREADY_MEMBER, targetName));
            member.get().setLevel(AWorldMember.TRUST_MEMBER);
            member.get().save();
        } else {
            world.addMember(targetUuid, AWorldMember.TRUST_MEMBER);
        }
        ChatHandler.chatConfirmation(sender, targetName + " is now a member of " + world.getName() + " world!");
    }
}
