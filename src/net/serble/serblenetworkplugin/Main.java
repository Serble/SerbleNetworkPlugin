package net.serble.serblenetworkplugin;

import com.google.gson.Gson;
import net.serble.serblenetworkplugin.Schemas.*;
import net.serble.serblenetworkplugin.Commands.*;
import net.serble.serblenetworkplugin.mysql.MySQL;
import net.serble.serblenetworkplugin.mysql.SQLGetter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

public class Main extends JavaPlugin {

    public static Main plugin;
    public static ConfigSave config;
    public static boolean hasConfig = false;
    public MySQL SQL;
    public static SQLGetter sqlData;

    @Override
    public void onEnable() {
        plugin = this;  // Allow access to the plugin from other classes

        SQL = new MySQL();  // Get an instance of the MySQL class
        sqlData = new SQLGetter();  // Get an instance of the SQL Getter class to access data

        Bukkit.getLogger().info("Connecting to MySQL...");

        try {
            SQL.connect();  // connect to MySQL
            sqlData.createTables();
        } catch (ClassNotFoundException | SQLException e) {
            // something failed
            Bukkit.getLogger().severe("MySQL connection failed:");
            Bukkit.getLogger().severe(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Bukkit.getLogger().info("MySQL connected!");

        // Register events
        Bukkit.getServer().getPluginManager().registerEvents(new Chat(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinLeave(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MenuItemManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LobbyManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MenuCommand(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ScoreboardManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new StoreCommand(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new NicknameManager(), this);

        // Register commands
        Objects.requireNonNull(this.getCommand("menu")).setExecutor(new MenuCommand());
        Objects.requireNonNull(this.getCommand("lob")).setExecutor(new MenuCommand());
        Objects.requireNonNull(this.getCommand("hub")).setExecutor(new MenuCommand());
        Objects.requireNonNull(this.getCommand("lobby")).setExecutor(new MenuCommand());
        Objects.requireNonNull(this.getCommand("adminmode")).setExecutor(new AdminMode());
        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new SpawnCmd());
        Objects.requireNonNull(this.getCommand("cosmetic")).setExecutor(new CosmeticCmd());
        Objects.requireNonNull(this.getCommand("ranknick")).setExecutor(new RankNickCmd());
        Objects.requireNonNull(this.getCommand("nick")).setExecutor(new NickCmd());
        Objects.requireNonNull(this.getCommand("unnick")).setExecutor(new UnnickCmd());
        Objects.requireNonNull(this.getCommand("build")).setExecutor(new BuildCmd());
        Objects.requireNonNull(this.getCommand("reloadconfig")).setExecutor(new ReloadConfigCommand());
        Objects.requireNonNull(this.getCommand("money")).setExecutor(new MoneyCommand());
        Objects.requireNonNull(this.getCommand("store")).setExecutor(new StoreCommand());
        Objects.requireNonNull(this.getCommand("shop")).setExecutor(new StoreCommand());
        Objects.requireNonNull(this.getCommand("sysgivemoney")).setExecutor(new SystemGiveMoney());
        Objects.requireNonNull(this.getCommand("play")).setExecutor(new PlayCommand());
        Objects.requireNonNull(this.getCommand("goto")).setExecutor(new PlayCommand());
        Objects.requireNonNull(this.getCommand("chatsudo")).setExecutor(new ChatSudoCommand());
        Objects.requireNonNull(this.getCommand("serblexp")).setExecutor(new SerbleXpCommand());
        Objects.requireNonNull(this.getCommand("sysgivexp")).setExecutor(new SystemGiveXp());
        Objects.requireNonNull(this.getCommand("grantachievementprogress")).setExecutor(new GrantAchievementProgressCommand());

        // Tab completions
        Objects.requireNonNull(this.getCommand("ranknick")).setTabCompleter(new RankNickCmd());
        Objects.requireNonNull(this.getCommand("play")).setTabCompleter(new PlayCommand());

        // register plugin messaging channels
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "serble:serble");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "serble:serble", new ConfigManager());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "calcilator:svtp");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "calcilator:svtp", new SVTPManager());

        this.saveDefaultConfig();  // Copy the config

        createConfigExample();  // Log the default bungee config

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L); // Start TPS Thing

        getLogger().info("Serble Network Plugin is now running!");  // Tell everyone it was enabled

        if (plugin.getConfig().getBoolean("override-bungee-config")) {
            String config = plugin.getConfig().getString("bungee-config");
            Gson json = new Gson();
            Main.config = json.fromJson(config, ConfigSave.class);
            Main.hasConfig = true;
            Main.plugin.getLogger().info("Obtained bungee config!");
        }
    }

    @Override
    public void onDisable() {

        SQL.disconnect();  // Disconnect MySQL

        // Unregister plugin messaging channel
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    public void createConfigExample() {
        ConfigSave save = new ConfigSave();

        save.ChatFormat = "[{world}] [{rank}] {name}: {message}";

        Rank rank = new Rank();
        rank.Name = "Default";
        rank.Display = "[Default]";
        rank.Permission = "serble.default";
        save.Ranks.add(rank);

        MenuItem mi = new MenuItem();
        mi.Name = "Menu";
        mi.Slot = 0;
        mi.Material = "COMPASS";
        mi.Commands.add("lob");
        save.MenuItems.add(mi);

        GameModeMenuItem gmmi = new GameModeMenuItem();
        gmmi.Name = "Lobby";
        gmmi.GameMode = "Lobby";
        gmmi.Material = "CLOCK";
        save.GameModeMenuItems.add(gmmi);

        StoreItem si = new StoreItem();
        si.Name = "Free money";
        si.Cost = 100;
        si.Permission = "serble.admin";
        si.Slot = 0;
        si.Material = "SUNFLOWER";
        si.Commands.add("sysgivemoney {player} 100 Here you go");
        save.StoreItems.add(si);

        GameMode mode = new GameMode();
        mode.Name = "Lobby";
        mode.Server = "main";
        mode.World = "world";
        mode.Permission = "serble.default";
        save.GameModes.add(mode);

        Gson json = new Gson();
        getLogger().info(json.toJson(save));
    }

}
