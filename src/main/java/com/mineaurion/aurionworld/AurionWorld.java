package com.mineaurion.aurionworld;

import com.mineaurion.aurionworld.core.Log;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.CommandManager;
//import com.mineaurion.aurionworld.core.database.Mysql;
import com.mineaurion.aurionworld.commands.AurionWorldCommand;
import com.mineaurion.aurionworld.core.models.World;
import com.mineaurion.aurionworld.world.AWorldManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;
//import org.javalite.activejdbc.Base;

import java.io.*;
import java.sql.SQLException;

@Mod(modid = AurionWorld.MODID, name = AurionWorld.NAME, version = AurionWorld.VERSION, acceptableRemoteVersions = "*")
@SideOnly(Side.SERVER)
public class AurionWorld {
    /*********************************************
     * Mod Configuration
     */
    static final String MODID = "aurionworld";
    static final String NAME = "AurionWorld";
    static final String VERSION = "1.0";
    /*********************************************
     * Plugin attributes
     */
    private static Configuration _configuration;
    private static CommandManager _commandManager;

    private static AWorldManager _worldManager;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        _configuration = new Configuration(new File("config/AurionWorld.cfg"));
        _configuration.load();

        // Cmd
        _configuration.setCategoryComment(Command.prefix, "Commands datas");

        // Database
        _configuration.setCategoryComment("database", "Database credentials");
        String host = _configuration.get("database", "host", "localhost").getString();
        String port = _configuration.get("database", "port", "3306").getString();
        String base = _configuration.get("database", "base", "aurionworld").getString();
        String username = _configuration.get("database", "username", "root").getString();
        String password = _configuration.get("database", "password", "root").getString();

        if (_configuration.hasChanged())
            _configuration.save();
        /*
        // Set MySQL settings
        Mysql.setCredentials(host, port, base, username, password);
        // Try to open Database with settings given
        Mysql.open();
        // Create schema if not exist
        Base.exec("CREATE DATABASE IF NOT EXISTS " + base);

        try {
            Base.connection().setCatalog(base);
            Log.info("Database set to `" + base + "`");
        } catch (SQLException e) {
            Log.error(e.getMessage());
        }

        // Run AurionWorld.sql
        Mysql.runFile(
                getClass().getClassLoader()
                        .getResourceAsStream("assets/aurionworld/aurionworld.sql")
        );*/
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        _worldManager  = new AWorldManager();

        _worldManager.loadWorldProviders();
        _worldManager.loadWorldTypes();
    }

    @Mod.EventHandler
    public void start(FMLServerStartingEvent event) {
        MinecraftServer srv = event.getServer();

        _worldManager.load();

        _commandManager = new CommandManager((ServerCommandManager) srv.getCommandManager());
        _commandManager.registerCommand(new AurionWorldCommand("aw"));
    }

    @Mod.EventHandler
    public void stop(FMLServerStoppedEvent event) {
        _worldManager.stop();
        //Mysql.close();
    }

    public static void sendMessage(ICommandSender commandSender, String message) {
        if (commandSender == MinecraftServer.getServer()) {
            Log.info(message);
            return;
        }
        while (message != null) {
            int nlIndex = message.indexOf('\n');
            String sent;
            if (nlIndex == -1) {
                sent = message;
                message = null;
            } else {
                sent = message.substring(0, nlIndex);
                message = message.substring(nlIndex + 1);
            }
            commandSender.addChatMessage(new ChatComponentText(sent));
        }
    }

    public static Configuration getConfig() {
        return _configuration;
    }

    public static AWorldManager getWorldManager() {
        return _worldManager;
    }

    public static void reload() {
        _configuration.load();
        _commandManager.reload();
        _configuration.save();

    }


}
