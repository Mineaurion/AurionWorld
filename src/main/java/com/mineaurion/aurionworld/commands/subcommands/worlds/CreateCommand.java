package com.mineaurion.aurionworld.commands.subcommands.worlds;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.SubCommand;
import com.mineaurion.aurionworld.world.AWorld;
import com.mineaurion.aurionworld.world.AWorldException;
import com.mineaurion.aurionworld.world.AWorldManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.*;
import net.minecraftforge.common.DimensionManager;


public class CreateCommand extends SubCommand {
    public CreateCommand(String id, Command parent) {
        super(id, parent);
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

    @Override
    public void process(ICommandSender sender, String[] args) {
        AurionWorld.sendMessage(sender, "Available worlds providers:");
        for (String provider : AurionWorld.getWorldManager().getWorldProviders().keySet())
        {
            AurionWorld.sendMessage(sender, "  " + provider);
        }

        AurionWorld.sendMessage(sender, "Available worlds types:");
        for (String worldType : AurionWorld.getWorldManager().getWorldTypes().keySet())
        {
            AurionWorld.sendMessage(sender, "  " + worldType);
        }

        AurionWorld.sendMessage(sender, "Available worlds:");
        for (AWorld world : AurionWorld.getWorldManager().getWorlds())
        {
            AurionWorld.sendMessage(sender, "#" + world.getDimensionId() + " " + world.getName() + ": " + world.getProvider());
        }

        String name = "Ashk-" + DimensionManager.getNextFreeDimId();
        String provider = AWorldManager.PROVIDER_NORMAL;
        String worldType = WorldType.DEFAULT.getWorldTypeName();
        long seed;

        AWorld world = new AWorld(name, provider, worldType);
        try {
            AurionWorld.getWorldManager().addWorld(world);
        } catch (AWorldException e) {
            e.printStackTrace();
        }
        //worlds.teleport((EntityPlayerMP)sender, true);
    }
}
