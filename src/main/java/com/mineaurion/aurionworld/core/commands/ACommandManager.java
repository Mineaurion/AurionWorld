package com.mineaurion.aurionworld.core.commands;

import net.minecraft.command.ServerCommandManager;

import java.util.ArrayList;
import java.util.List;

public class ACommandManager {
    private ServerCommandManager _scm;
    private List<ACommand> _commands;

    public ACommandManager(ServerCommandManager scm) {
        _scm = scm;
        _commands = new ArrayList<>();
    }

    public void registerCommand(ACommand command) {
        _scm.registerCommand(command);
        _commands.add(command);
    }

    public void reload() {
        for (ACommand c : _commands) {
            c.reload();
        }
    }
}
