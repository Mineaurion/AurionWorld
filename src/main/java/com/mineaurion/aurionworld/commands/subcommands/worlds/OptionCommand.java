package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.ACommand;
import com.mineaurion.aurionworld.core.commands.ACommandException;
import com.mineaurion.aurionworld.core.commands.ACommandSub;
import com.mineaurion.aurionworld.core.commands.AUsageException;
import com.mineaurion.aurionworld.core.misc.WorldUtil;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameRules;

import java.util.Arrays;
import java.util.Optional;

public class OptionCommand extends ACommandSub {
    public OptionCommand(String id, ACommand parent) {
        super(id, parent);
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length < 2)
            throw new AUsageException(AUsageException.NOT_ENOUGH);
        if (args.length > 3)
            throw new AUsageException(AUsageException.TOO_MANY);

        Optional<AWorld> world;
        String rule;
        String value;
        boolean beInside;

        // Player is in world ?
        if (args.length == 2) {
            world = WorldUtil.whereIsPlayer((EntityPlayerMP) sender);
            rule = args[0];
            value = args[1];
            beInside = true;
        // Player give a name AWorld
        } else {
            world = AurionWorld.getWorldManager().getWorld(args[0]);
            rule = args[1];
            value = args[2];
            beInside = false;
        }

        // Check if AWorld not exist
        if (!world.isPresent())
            throw new ACommandException(
                    (args.length == 2)
                            ? ACommandException.NOT_ALLOWED
                            : ACommandException.WORLD_NOT_EXIST
            );

        if (!world.get().canDoOwnerAction(sender, beInside))
            throw new ACommandException(ACommandException.NOT_ALLOWED);

        GameRules rules = world.get().getWorldServer().getGameRules();
        addTabCompletionOptions(sender, rules.getRules());
        if (!rules.hasRule(rule))
            throw new ACommandException(String.format(ACommandException.RULE_NOT_EXIST, rule));


        rules.setOrCreateGameRule(rule, value);
        ChatHandler.chatConfirmation(sender,String.format("Set gamerule %s = %s for world %s", rule, value, world.get().getName()));
    }
}
