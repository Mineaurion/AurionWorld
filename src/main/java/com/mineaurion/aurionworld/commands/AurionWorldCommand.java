package com.mineaurion.aurionworld.commands;

import com.mineaurion.aurionworld.commands.subcommands.utils.HelpCommand;
import com.mineaurion.aurionworld.commands.subcommands.utils.ReloadCommand;
import com.mineaurion.aurionworld.commands.subcommands.worlds.CreateCommand;
import com.mineaurion.aurionworld.commands.subcommands.worlds.DeleteCommand;
import com.mineaurion.aurionworld.commands.subcommands.worlds.LoadCommand;
import com.mineaurion.aurionworld.commands.subcommands.worlds.UnloadCommand;
import com.mineaurion.aurionworld.core.commands.Command;
import net.minecraft.command.ICommandSender;

public class AurionWorldCommand extends Command {

    public AurionWorldCommand(String id) {
        super(id);
        // utils
        setSubCommand(new ReloadCommand("reload", this));
        setSubCommand(new HelpCommand("help", this));
        // worlds
        setSubCommand(new CreateCommand("create", this));
        setSubCommand(new DeleteCommand("delete", this));
        setSubCommand(new LoadCommand("load", this));
        setSubCommand(new UnloadCommand("unload", this));
    }

    @Override
    public void process(ICommandSender commandSender, String[] args) {
        getSubCommandById("help").execute(commandSender, args);
    }


}
