package com.mineaurion.aurionworld.core.commands;

import net.minecraft.command.ServerCommandManager;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private ServerCommandManager _scm;
    private List<Command> _commands;

    public CommandManager(ServerCommandManager scm) {
        _scm = scm;
        _commands = new ArrayList<>();
    }

    public void registerCommand(Command command) {
        _scm.registerCommand(command);
        _commands.add(command);
    }

    public void reload() {
        for (Command c : _commands) {
            c.reload();
        }
    }
}
