package com.mineaurion.api.commands;

import com.mineaurion.api.Log;
import com.mineaurion.aurionworld.AurionWorld;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;

import java.util.*;

public abstract class Command extends CommandBase {
    public static String prefix = "cmd";

    private Configuration _conf;

    private String _package;
    private String _id;
    protected Command _parent;
    private Map<String, SubCommand> _subCommands;

    private String _name;
    private ArrayList<String> _aliases;
    private String _usage;
    private String _description;
    private int _permission;

    private boolean _onlyPlayer = false;

    public Command(String id) {
        super();
        _conf = AurionWorld.getConfig();

        _parent = null;
        _package = prefix;
        _id = id;

        _name = id;
        _subCommands = new HashMap<>();
        init();
    }

    Command(String id, Command parent) {
        super();
        _conf = AurionWorld.getConfig();

        _parent = parent;
        _package = parent.getPackage() + "." + parent.getId();
        _id = id;

        _name = id;
        _subCommands = new HashMap<>();
        init();
    }

    public void reload() {
        init();
        _subCommands.forEach((k, v) -> {
            v.reload();
        });
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
        AurionWorld.sendMessage(sender, _usage);
        return _usage;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        try {
            if (!isPlayer(sender) && _onlyPlayer)
                throw new UsageException("This command is only for EntityPlayer!");
        } catch (UsageException ue) {
            AurionWorld.sendMessage(sender, "This command is only for EntityPlayer!");
            return;
        }

        Log.info(getFullId() + " executed!");
        // Try to run Subcommand if exist!
        try {
            if (hasSubCommands() && args.length >= 1) {
                SubCommand subCommand = getSubCommand(args[0]);
                // Remove the first arguments (subcommand arg)
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                if (subCommand != null) {
                    subCommand.execute(sender, newArgs);
                    return;
                }
                else
                    throw new UsageException();
            }
        } catch (UsageException ue) {
            Log.error(ue.getMessage());
            AurionWorld.sendMessage(sender, getCommandUsage(sender));
        }
        process(sender, args);
    }

    public void execute(ICommandSender sender, String[] args) {
        processCommand(sender, args);
    }

    protected abstract void process(ICommandSender sender, String[] args);

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return _permission;
    }

    public boolean hasSubCommands() {
        return !_subCommands.isEmpty();
    }

    public String getId() {
        return _id;
    }

    public String getFullId() {
        return _package + "." + _id;
    }

    public String getPackage() {
        return _package;
    }

    public SubCommand setSubCommand(SubCommand sub) {
        _subCommands.put(sub.getId(), sub);
        return sub;
    }

    public SubCommand getSubCommandById(String id) {
        if (!hasSubCommands())
            return null;
        return _subCommands.get(id);
    }

    public SubCommand getSubCommand(String cmdNameOrAliases) {
        if (!hasSubCommands())
            return null;

        for(Map.Entry<String, SubCommand> entry : _subCommands.entrySet()) {
            SubCommand tmp = entry.getValue();
            if (cmdNameOrAliases.equals(tmp.getCommandName()) || tmp.getCommandAliases().contains(cmdNameOrAliases))
                return tmp;
        }

        return null;
    }

    public Command onlyPlayer(boolean only) {
        _onlyPlayer = only;
        return this;
    }

    private void init() {
        try {
            String[] defaultAliases = new String[]{_name + "2"};

            _name = _conf.get(getFullId(), "name", _name).getString();
            _aliases = new ArrayList<String>(
                    Arrays.asList(_conf.get(getFullId(), "aliases", defaultAliases).getStringList())
            );

            _usage = _conf.get(getFullId(), "usage", "Usage " + getFullId()).getString();
            _description = _conf.get(getFullId(), "description", "Description " + getFullId()).getString();
            _permission = _conf.get(getFullId(), "permission", 1).getInt();

            if (_conf.hasChanged())
                _conf.save();

        } catch (Exception e) {
            Log.error("Can't init command '" + getFullId() + "' " + e.getMessage());
        }
    }

    private boolean isServer(ICommandSender sender) {
        return sender == MinecraftServer.getServer();
    }

    private boolean isPlayer(ICommandSender sender) {
        return (sender instanceof EntityPlayer);
    }
}
