package com.mineaurion.aurionworld.core.commands;

import com.mineaurion.aurionworld.core.misc.output.ChatHandler;
import com.mineaurion.aurionworld.core.misc.output.Log;
import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.world.AWorldException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;

import java.util.*;

public abstract class ACommand extends CommandBase {
    public static String prefix = "cmd";

    private String _package;
    private String _id;
    protected ACommand _parent;
    private Map<String, ACommandSub> _subCommands;

    private String _name;
    private ArrayList<String> _aliases;
    private String _usage;
    private String _description;
    private int _permission = 0;

    public ACommand(String id) {
        super();

        _parent = null;
        _package = prefix;
        _id = id;

        _name = id;
        _subCommands = new HashMap<>();
        init();
    }

    ACommand(String id, ACommand parent) {
        super();

        _parent = parent;
        _package = parent.getPackage() + "." + parent.getId();
        _id = id;

        _name = id;
        _subCommands = new HashMap<>();
        init();
    }

    public void reload() {
        init();
        Log.info("Command " + getFullId() + " reloaded");
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
        return _usage;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return (_permission == 0) || AurionWorld.isOp(sender);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        try {
            if (!AurionWorld.isPlayer(sender) && isPlayerOnly())
                throw new AUsageException("This command is only for EntityPlayer!");
        } catch (AUsageException ue) {
            ChatHandler.sendMessage(sender, "This command is only for EntityPlayer!");
            return;
        }

        Log.debug(getFullId() + " executed!");
        // Try to run Subcommand if exist!
        ACommandSub subCommand = null;
        try {
            if (hasSubCommands() && args.length >= 1) {
                subCommand = getSubCommand(args[0]);
                // Remove the first arguments (subcommand arg)
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                if (subCommand != null) {
                    subCommand.preProcess(sender, newArgs);
                    return;
                }
                else
                    throw new AUsageException(AUsageException.UNKNOW);
            }
            process(sender, args);
        } catch (AUsageException ue) {
            String message = (ue.getMessage() == null) ? AUsageException.UNKNOW : ue.getMessage();
            ChatHandler.chatError(sender, message);
            if (subCommand != null) {
                ChatHandler.sendMessage(sender, subCommand.getCommandUsage(sender));
            }
        } catch (ACommandException | AWorldException ex ) {
            ChatHandler.chatError(sender, ex.getMessage());
        }
    }

    protected abstract void process(ICommandSender sender, String[] args) throws ACommandException, AUsageException, AWorldException;

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

    public ACommandSub setSubCommand(ACommandSub sub) {
        _subCommands.put(sub.getId(), sub);
        return sub;
    }

    public ACommandSub getSubCommandById(String id) {
        if (!hasSubCommands())
            return null;
        return _subCommands.get(id);
    }

    public ACommandSub getSubCommand(String cmdNameOrAliases) {
        if (!hasSubCommands())
            return null;

        for(Map.Entry<String, ACommandSub> entry : _subCommands.entrySet()) {
            ACommandSub tmp = entry.getValue();
            if (cmdNameOrAliases.equals(tmp.getCommandName()) || tmp.getCommandAliases().contains(cmdNameOrAliases))
                return tmp;
        }

        return null;
    }

    public boolean isPlayerOnly() {
        return false;
    }

    private void init() {
        try {
            Configuration conf = AurionWorld.getConfig();
            String[] defaultAliases = new String[]{_name + "2"};

            _name = conf.get(getFullId(), "name", _name).getString();
            _aliases = new ArrayList<String>(
                    Arrays.asList(conf.get(getFullId(), "aliases", defaultAliases).getStringList())
            );
            _usage = conf.get(getFullId(), "usage", "Usage " + getFullId()).getString();
            _description = conf.get(getFullId(), "description", "Description " + getFullId()).getString();
            _permission = conf.get(getFullId(), "permission", 0).getInt();

            if (conf.hasChanged())
                conf.save();
        } catch (Exception e) {
            Log.error("Can't init command '" + getFullId() + "' " + e.getMessage());
        }
    }
}
