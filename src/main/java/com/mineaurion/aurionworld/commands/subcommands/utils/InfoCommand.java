package com.mineaurion.aurionworld.commands.subcommands.utils;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class InfoCommand extends SubCommand {
    public InfoCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length != 1)
            return;

        String playerName = args[0];
        Optional<UUID> uuid = AurionWorld.getPlayerUuid(playerName);

        // Player doesn't exist
        if (!uuid.isPresent()) {
            AurionWorld.sendMessage(sender, "Player " + playerName + "doesnt'exist!");
            return;
        }

        Collection<AWorld> playerWorlds = AurionWorld.getWorldManager().getPlayerWorlds(uuid.get());
        if (!playerWorlds.isEmpty()) {
            StringBuilder info = new StringBuilder(playerName + "worlds infos ("+ playerWorlds.size() +")");
            for (AWorld w : playerWorlds) {
                info.append("\n-----------------------------");
                info.append("\n- DimId : ");
                info.append(w.getDimensionId());
                info.append(" ; - Name : ");
                info.append(w.getName());
                info.append(" ; - Provider : ");
                info.append(w.getProvider());
                info.append(" ; - Type : ");
                info.append(w.getWorldType());
            }
            AurionWorld.sendMessage(sender, info.toString());
        }  else {
            AurionWorld.sendMessage(sender, "Player " + playerName + " isn't attached to any world!");
        }
    }
}
