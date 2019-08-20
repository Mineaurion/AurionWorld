package com.mineaurion.aurionworld;

import com.mineaurion.aurionworld.core.misc.output.Log;
import com.mineaurion.aurionworld.core.commands.Command;
import com.mineaurion.aurionworld.core.commands.CommandManager;
//import com.mineaurion.aurionworld.core.database.Mysql;
import com.mineaurion.aurionworld.commands.AurionWorldCommand;
import com.mineaurion.aurionworld.core.database.Mysql;
import com.mineaurion.aurionworld.world.AWorldManager;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.UserListOps;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.common.config.Configuration;
import org.javalite.activejdbc.Base;
import scala.tools.ant.sabbus.Use;
//import org.javalite.activejdbc.Base;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

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
        );


    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        _worldManager = new AWorldManager();
        FMLCommonHandler.instance().bus().register(_worldManager);
    }

    @Mod.EventHandler
    public void start(FMLServerStartingEvent event) {
        MinecraftServer srv = event.getServer();

        _worldManager.loadWorldProviders();
        _worldManager.loadWorldTypes();
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

    public static void sendMessage(ICommandSender sender, ChatComponentText message) {
        sender.addChatMessage(message);
    }

    public static Configuration getConfig() {
        return _configuration;
    }

    public static AWorldManager getWorldManager() {
        return _worldManager;
    }

    public static List<EntityPlayerMP> getPlayerList() {
        MinecraftServer mc = MinecraftServer.getServer();
        return mc == null || mc.getConfigurationManager() == null ? new ArrayList<>() : mc.getConfigurationManager().playerEntityList;
    }

    public static void reload() {
        _configuration.load();
        _commandManager.reload();
        _configuration.save();

    }

    public static boolean isServer(ICommandSender sender) {
        return sender == MinecraftServer.getServer();
    }

    public static boolean isPlayer(ICommandSender sender) {
        return (sender instanceof EntityPlayer);
    }

    public static boolean isOp(ICommandSender sender) {
        if (AurionWorld.isServer(sender))
            return true;

        String playerName = sender.getCommandSenderName();
        UserListOps opsList = MinecraftServer.getServer().getConfigurationManager().func_152603_m();
        return opsList.func_152700_a(playerName) != null;
    }


    public static Optional<String> getPlayerName(UUID uuid) {
        if (UsernameCache.containsUUID(uuid))
            return Optional.empty();
        return Optional.ofNullable(UsernameCache.getLastKnownUsername(uuid));
    }

    public static Optional<UUID> getPlayerUuid(String name) {
        for (Map.Entry<UUID, String> entry : UsernameCache.getMap().entrySet()) {
            if (entry.getValue().equals(name))
                return Optional.ofNullable(entry.getKey());
        }
        return Optional.empty();
    }
}
