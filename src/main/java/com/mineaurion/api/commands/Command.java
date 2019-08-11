package com.mineaurion.api.commands;

import com.mineaurion.aurionworld.AurionWorld;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Command extends CommandBase {
    protected AurionWorld plugin;

    protected String _name;
    protected List<String> _aliases;
    protected String _usage;
    protected int _requireLevel;

    protected Map<String, SubCommand> _subCommands;

    public Command(String name, int requireLevel, String... aliases) {
        super();
        _name = name;
        _requireLevel = requireLevel;
        _aliases = Arrays.asList(aliases);
        _subCommands = new HashMap<>();
    }

    @Override
    public String getCommandName() {
        return _name;
    }

    @Override
    public List getCommandAliases() {
        return _aliases;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return _usage;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] strings) {

    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] strings) {
        return null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return _requireLevel;
    }
}
