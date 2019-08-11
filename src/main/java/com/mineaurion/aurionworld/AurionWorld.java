package com.mineaurion.aurionworld;

import com.mineaurion.api.Log;
import com.mineaurion.api.commands.Levels;
import com.mineaurion.api.database.Mysql;
import com.mineaurion.api.database.ScriptRunner;
import com.mineaurion.aurionworld.commands.AurionWorldCommand;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import org.javalite.activejdbc.Base;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

@Mod(modid = AurionWorld.MODID, name = AurionWorld.NAME, version = AurionWorld.VERSION)
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
    private Configuration _configuration;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        _configuration = new Configuration(new File("config/AurionWorld.cfg"));
        _configuration.load();

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

        Log.info("Database `" + base + "` has been created!");

        Connection con = Base.connection();
        ScriptRunner runner = new ScriptRunner(con, false, false);
        InputStream in = getClass().getClassLoader().getResourceAsStream("assets/aurionworld/aurionworld.sql");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            runner.runScript(new BufferedReader(reader));
            reader.close();
        } catch (IOException | SQLException e) {
            Log.error(e.getMessage());
        }
    }


    @Mod.EventHandler
    public void start(FMLServerStartingEvent event) {
        MinecraftServer srv = event.getServer();

        ServerCommandManager scm = (ServerCommandManager) srv.getCommandManager();
        scm.registerCommand(new AurionWorldCommand("test", Levels.PLAYER, "toto", "tata"));
    }

    @Mod.EventHandler
    public void stop(FMLServerStoppedEvent event) {
        Mysql.close();
    }

    public Configuration getConfig() {
        return _configuration;
    }
}
