package com.mineaurion.aurionworld.commands.subcommands.members;

import com.mineaurion.aurionworld.core.commands.ACommand;
import com.mineaurion.aurionworld.core.commands.ACommandTrust;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.world.AWorldMember;
import net.minecraft.command.ICommandSender;

import java.util.Optional;

public class UntrustCommand extends ACommandTrust {
    public UntrustCommand(String id, ACommand parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (!checkProcess(sender, args)) {
            return;
        }

        Optional<AWorldMember> member = world.getMember(targetUuid);
        if (member.isPresent()) {
            world.removeMember(targetUuid);
        } else {
            ChatHandler.chatError(sender, "This user isn't attached to " + world.getName() + " world!");
            return;
        }
        ChatHandler.chatConfirmation(sender, targetName + " is no longer trusted on  " + world.getName() + " world!");
    }
}
