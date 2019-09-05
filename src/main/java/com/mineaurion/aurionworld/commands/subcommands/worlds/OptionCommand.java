package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.core.misc.WorldUtil;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldManager;

import java.util.Arrays;
import java.util.Optional;

public class OptionCommand extends SubCommand {
    public OptionCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            ChatHandler.chatError(sender, "Not enough params");
            return;
        }
        if (args.length > 3) {
            ChatHandler.chatError(sender, "Too many params");
            return;
        }

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
        if (!world.isPresent()) {
            String error_message = (args.length == 2) ? "Not allowed to do this in this world!" : "This world doesn't exist";
            ChatHandler.chatError(sender, error_message);
            return;
        }

        if (!world.get().canDoOwnerAction(sender, beInside)) {
            ChatHandler.chatError(sender, "Not allowed to do this in this world!");
            return;
        }

        GameRules rules = world.get().getWorldServer().getGameRules();
        this.addTabCompletionOptions(sender, rules.getRules());
        if (!rules.hasRule(rule)) {
            ChatHandler.chatError(sender, "Rule " + rule + " doesn't exist!");
            ChatHandler.chatNotification(sender, "Existing rules :");
            ChatHandler.chatNotification(sender, Arrays.toString(rules.getRules()));
            return;
        }

        rules.setOrCreateGameRule(rule, value);
        ChatHandler.chatConfirmation(sender,String.format("Set gamerule %s = %s for world %s", rule, value, world.get().getName()));
    }
}
