package com.mineaurion.aurionworld.commands.subcommands.utils;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
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
            ChatHandler.sendMessage(sender, "Player " + playerName + "doesnt'exist!");
            return;
        }

        Collection<AWorld> playerWorlds = AurionWorld.getWorldManager().getPlayerWorlds(uuid.get());
        if (!playerWorlds.isEmpty()) {
            StringBuilder info = new StringBuilder(playerName + "worlds infos ("+ playerWorlds.size() +")");
            for (AWorld w : playerWorlds) {
                info.append("\n-----------------------------");
                info.append("\n- DimId : ");
                info.append(w.getDimensionId());
                info.append("\n- Name : ");
                info.append(w.getName());
                info.append("\n- Provider : ");
                info.append(w.getProvider());
                info.append("\n- Type : ");
                info.append(w.getWorldType());
                info.append("\n- Structures : ");
                info.append(w.isStructures());
                info.append("\n- Loaded : ");
                info.append(w.isLoaded());
                info.append("\n- Load it : ");
                info.append(w.isLoadIt());
                info.append("\n- Seed : ");
                info.append(w.getSeed());
                info.append("\n- Generator : ");
                info.append(w.getGenerator());
            }
            ChatHandler.sendMessage(sender, info.toString());
        }  else {
            ChatHandler.sendMessage(sender, "Player " + playerName + " isn't attached to any world!");
        }
    }
}
