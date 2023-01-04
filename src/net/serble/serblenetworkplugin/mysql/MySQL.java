package net.serble.serblenetworkplugin.mysql;

import net.serble.serblenetworkplugin.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
    private Connection connection;

    public boolean isConnected() {
        return (connection != null);
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if (isConnected()) return;

        String host = Main.plugin.getConfig().getString("serverurl");
        String port = Main.plugin.getConfig().getString("serverport");
        String database = Main.plugin.getConfig().getString("database");
        String username = Main.plugin.getConfig().getString("user");
        String password = Main.plugin.getConfig().getString("password");

        connection = DriverManager.getConnection("jdbc:mysql://" +
                        host + ":" + port + "/" + database, //+ "?autoReconnect=true&useSSL=false",
                username, password);
    }

    public void disconnect() {
        if (!isConnected()) return;

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
