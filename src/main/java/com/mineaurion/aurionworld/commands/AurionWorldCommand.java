package com.mineaurion.aurionworld.commands;

import com.mineaurion.api.commands.Command;
import com.mineaurion.aurionworld.commands.world.WorldCommand;
import net.minecraft.command.ICommandSender;

public class AurionWorldCommand extends Command {

    public AurionWorldCommand(String id) {
        super(id);
        setSubCommand(new ReloadCommand("reload", this));
        setSubCommand(new HelpCommand("help", this));
        setSubCommand(new WorldCommand("world", this));
    }

    @Override
    public void process(ICommandSender commandSender, String[] args) {
        getSubCommandById("help").execute(commandSender, args);

    }


}
