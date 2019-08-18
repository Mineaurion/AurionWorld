package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.Log;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.world.AWorld;
import com.mineaurion.aurionworld.world.AWorldException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Random;


public class CreateCommand extends SubCommand {
    public CreateCommand(String id, Command parent) {
        super(id, parent);
    }

    public void listsInfo(ICommandSender sender) {
        AurionWorld.sendMessage(sender, "Available worlds providers:");
        for (String provider : AurionWorld.getWorldManager().getWorldProviders().keySet())
            AurionWorld.sendMessage(sender, "  " + provider.toLowerCase());

        AurionWorld.sendMessage(sender, "Available worlds types:");
        for (String worldType : AurionWorld.getWorldManager().getWorldTypes().keySet())
            AurionWorld.sendMessage(sender, "  " + worldType.toLowerCase());

        AurionWorld.sendMessage(sender, getCommandUsage(sender));
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
            listsInfo(sender);
            return;
        }

        if (args.length < 3) {
            Log.error("Not enough params");
            return;
        }

        EntityPlayerMP player = AurionWorld.getEntityPlayer(args[0]);

        if (player == null) {
            AurionWorld.sendMessage(sender, "Wooow, player " + args[0] + " doesn't exist!");
            return;
        }

        String name = args[0];
        String ownerUuid = player.getUniqueID().toString();
        String provider = args[1];//AWorldManager.PROVIDER_NORMAL;
        String worldType = args[2];//WorldType.DEFAULT.getWorldTypeName();
        long seed = (args.length >= 4) ? Long.getLong(args[3]) : new Random().nextLong();
        String generator = (args.length >= 5) ? args[4] : "";
        boolean structures = (args.length >= 6) && Boolean.getBoolean(args[5]);

        AWorld world = new AWorld(name, ownerUuid, provider, worldType, seed, generator, structures);
        try {
            AurionWorld.getWorldManager().addWorld(world);
        } catch (AWorldException e) {
            e.printStackTrace();
        }
        //worlds.teleport((EntityPlayerMP)sender, true);
    }
}
