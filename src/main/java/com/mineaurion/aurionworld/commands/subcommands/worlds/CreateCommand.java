package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.core.misc.output.Log;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.world.AWorld;
import com.mineaurion.aurionworld.world.AWorldException;
import net.minecraft.command.ICommandSender;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class CreateCommand extends SubCommand {
    public CreateCommand(String id, Command parent) {
        super(id, parent);
    }

    public void listsInfo(ICommandSender sender) {
        ChatHandler.sendMessage(sender, "Available worlds providers:");
        for (String provider : AurionWorld.getWorldManager().getWorldProviders().keySet())
            ChatHandler.sendMessage(sender, "  " + provider.toLowerCase());

        ChatHandler.sendMessage(sender, "Available worlds types:");
        for (String worldType : AurionWorld.getWorldManager().getWorldTypes().keySet())
            ChatHandler.sendMessage(sender, "  " + worldType.toLowerCase());

        ChatHandler.sendMessage(sender, getCommandUsage(sender));
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

        String name = args[0];
        Optional<UUID> uuid = AurionWorld.getPlayerUuid(name);

        if (!uuid.isPresent()) {
            ChatHandler.sendMessage(sender, "Wooow, player " + name + " doesn't exist!");
            return;
        }

        UUID ownerUuid = uuid.get();
        String provider = args[1];
        String worldType = args[2];
        long seed = (args.length >= 4) ? Long.parseLong(args[3]) : new Random().nextLong();
        String generator = (args.length >= 5) ? args[4] : "";
        boolean structures = (args.length >= 6) && Boolean.parseBoolean(args[5]);

        AWorld world = new AWorld(name, ownerUuid, provider, worldType, seed, generator, structures);
        try {
            AurionWorld.getWorldManager().addWorld(world);
            ChatHandler.sendMessage(sender, "World " + world.getName() + " has been created succesfully!");
        } catch (AWorldException e) {
            e.printStackTrace();
        }
    }
}
