package com.mineaurion.api.database;

import com.mineaurion.api.Log;
import org.apache.logging.log4j.Logger;
import org.javalite.activejdbc.Base;


public class Mysql {
    private static Mysql _instance = null;
    private String _driver = "com.mysql.cj.jdbc.Driver";

    /***********************************************
     * Credentials
     */
    private String _url = "jdbc:mysql://";
    private static String _host;
    private static String _port;
    private static String _base;
    private static String _username;
    private static String _password;

    private static boolean _init = false;

    public static Mysql open() {
        Mysql result = null;
        if (_instance == null)
            return new Mysql();
        return _instance;

    }

    public static void setCredentials(String host, String port, String base, String username, String password) {
        _host = host;
        _port = port;
        _base = base;
        _username = username;
        _password = password;

        _init = true;
    }

    private Mysql() {
        _url += _host + ":" + _port + "/?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC";
        try {
            Base.open(_driver, _url, _username, _password);
            Log.info("Database connection enabled");
            _instance = this;
        }
        catch (Exception e) {
            Log.error(e.getMessage());
        }
    }

    public static void close() {
        try {
            Base.close();
            Log.info("Database connection successfully closed");
        } catch (Exception e) {
            Log.error(e.getMessage());
        }
    }
}
