package com.mineaurion.aurionworld.commands;

import com.mineaurion.aurionworld.commands.subcommands.members.TrustMemberCommand;
import com.mineaurion.aurionworld.commands.subcommands.members.TrustOwnerCommand;
import com.mineaurion.aurionworld.commands.subcommands.members.UntrustCommand;
import com.mineaurion.aurionworld.commands.subcommands.utils.HelpCommand;
import com.mineaurion.aurionworld.commands.subcommands.utils.InfoCommand;
import com.mineaurion.aurionworld.commands.subcommands.utils.ReloadCommand;
import com.mineaurion.aurionworld.commands.subcommands.worlds.*;
import com.mineaurion.aurionworld.core.commands.ACommand;
import net.minecraft.command.ICommandSender;

public class AurionWorldCommand extends ACommand {

    public AurionWorldCommand(String id) {
        super(id);

        // members
        setSubCommand(new TrustMemberCommand("trustmember", this));
        setSubCommand(new TrustOwnerCommand("trustowner", this));
        setSubCommand(new UntrustCommand("untrust", this));
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
