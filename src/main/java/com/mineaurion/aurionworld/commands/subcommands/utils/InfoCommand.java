package com.mineaurion.aurionworld.commands.subcommands.utils;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.world.AWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;

import java.util.Collection;

public class InfoCommand extends SubCommand {
    public InfoCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length != 1)
            return;

        String arg = args[0];
        Collection<AWorld> playerOwnedWorlds = AurionWorld.getWorldManager().getPlayerOwnedWorlds(arg);
        AWorld world;
        if (!playerOwnedWorlds.isEmpty()) {
            StringBuilder info = new StringBuilder("Worlds owned by '" + arg + "' (" + playerOwnedWorlds.size() + ")");
            for (AWorld w : playerOwnedWorlds) {
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
        } else {
            try {
                int dimId = Integer.valueOf(arg);
                world = AurionWorld.getWorldManager().getWorld(dimId);
                if (world == null)
                    throw new Exception();
            } catch (Exception e) {
                AurionWorld.sendMessage(sender, "This world doesn't exist!");
            }
        }
    }
}
