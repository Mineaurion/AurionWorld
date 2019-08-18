package com.mineaurion.aurionworld.commands;

import com.mineaurion.aurionworld.commands.subcommands.members.AddMemberCommand;
import com.mineaurion.aurionworld.commands.subcommands.members.AddOwnerCommand;
import com.mineaurion.aurionworld.commands.subcommands.members.RemoveMemberCommand;
import com.mineaurion.aurionworld.commands.subcommands.members.RemoveOwnerCommand;
import com.mineaurion.aurionworld.commands.subcommands.utils.HelpCommand;
import com.mineaurion.aurionworld.commands.subcommands.utils.InfoCommand;
import com.mineaurion.aurionworld.commands.subcommands.utils.ReloadCommand;
import com.mineaurion.aurionworld.commands.subcommands.worlds.*;
import com.mineaurion.aurionworld.core.commands.Command;
import net.minecraft.command.ICommandSender;

public class AurionWorldCommand extends Command {

    public AurionWorldCommand(String id) {
        super(id);
        // members
        setSubCommand(new AddMemberCommand("addmember", this));
        setSubCommand(new AddOwnerCommand("addowner", this));
        setSubCommand(new RemoveMemberCommand("removemember", this));
        setSubCommand(new RemoveOwnerCommand("removeowner", this));
        // utils
        setSubCommand(new HelpCommand("help", this));
        setSubCommand(new InfoCommand("info", this));
        setSubCommand(new ReloadCommand("reload", this));
        // worlds
        setSubCommand(new CreateCommand("create", this));
        setSubCommand(new DeleteCommand("delete", this));
        setSubCommand(new LoadCommand("load", this));
        setSubCommand(new OptionCommand("option", this));
        setSubCommand(new SetSpawnCommand("setspawn", this));
        setSubCommand(new TeleportCommand("teleport", this));
        setSubCommand(new UnloadCommand("unload", this));
    }

    @Override
    public void process(ICommandSender commandSender, String[] args) {
        getSubCommandById("help").execute(commandSender, args);
    }


}
