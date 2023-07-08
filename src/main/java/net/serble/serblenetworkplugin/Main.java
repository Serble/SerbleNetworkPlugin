package net.serble.serblenetworkplugin;

import com.google.gson.Gson;
import net.serble.serblenetworkplugin.API.*;
import net.serble.serblenetworkplugin.Cache.CacheInvalidationManager;
import net.serble.serblenetworkplugin.Commands.CommandHandler;
import net.serble.serblenetworkplugin.Commands.Executors.AchievementsCommand;
import net.serble.serblenetworkplugin.Commands.Executors.MenuCommand;
import net.serble.serblenetworkplugin.Commands.Executors.StoreCommand;
import net.serble.serblenetworkplugin.Schemas.Config.*;
import net.serble.serblenetworkplugin.mysql.MySQL;
import net.serble.serblenetworkplugin.mysql.SQLGetter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.List;

public class Main extends JavaPlugin {

    public static Main plugin;
    public static ConfigSave config;
    public static boolean hasConfig = false;
    public MySQL SQL;
    public static SQLGetter sqlData;
    public static WorldGroupInventoryManager worldGroupInventoryManager;
    public static PartyManager partyManager;
    public static PartyService partyService;

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

        worldGroupInventoryManager = new WorldGroupInventoryManager();
        partyManager = new PartyManager();
        partyService = new PartyService();

        ServicesManager servicesManager = getServer().getServicesManager();
        servicesManager.register(IdService.class, new IdServiceImpl(), this, ServicePriority.Normal);
        servicesManager.register(DebugService.class, new DebugManager(), this, ServicePriority.Normal);
        servicesManager.register(InventoryManagementService.class, new InventoryManagementService(worldGroupInventoryManager), this, ServicePriority.Normal);
        servicesManager.register(PartyService.class, partyService, this, ServicePriority.Normal);

        // Register events
        Bukkit.getServer().getPluginManager().registerEvents(new Chat(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinLeave(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MenuItemManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LobbyManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MenuCommand(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ScoreboardManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new StoreCommand(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new NicknameManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CustomAchievements(), this);
        Bukkit.getServer().getPluginManager().registerEvents(worldGroupInventoryManager, this);
        Bukkit.getServer().getPluginManager().registerEvents(partyManager, this);
        Bukkit.getServer().getPluginManager().registerEvents(new ProfilePermissionsManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CacheInvalidationManager(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new AchievementsCommand(), this);

        // Register commands
        new CommandHandler().registerCommands();
        Bukkit.getLogger().info("Commands registered!");

        // Register plugin messaging channels
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "serble:serble");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "serble:serble", new ConfigManager());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "serble:proxyexecute");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "serble:party");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "serble:party", partyManager);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "calcilator:svtp");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "calcilator:svtp", new SVTPManager());

        this.saveDefaultConfig();  // Copy the config

        createConfigExample();  // Log the default bungee config

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TpsTracker(), 100L, 1L); // Start TPS Thing

        getLogger().info("Serble Network Plugin is now running!");  // Tell everyone it was enabled

        if (Bukkit.getOnlinePlayers().size() > 0) {
            // It was a reload
            boolean didRequestConfig = false;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!didRequestConfig) {
                    ConfigManager.requestConfig(p);
                    didRequestConfig = true;
                }
                worldGroupInventoryManager.loadPlayerInventory(p);
                DebugManager.getInstance().debug(p, "Loaded your inventory!");
                if (p.hasPermission("serble.staff")) {
                    DebugManager.getInstance().debug(p, "&bReloaded the plugin!");
                }
            }

            if (plugin.getConfig().contains("reloadcommands")) {
                List<String> reloadCmds = plugin.getConfig().getStringList("reloadcommands");
                for (String cmd : reloadCmds) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }
            }
        } else {
            // It was a startup
            if (plugin.getConfig().contains("startcommands")) {
                List<String> reloadCmds = plugin.getConfig().getStringList("startcommands");
                for (String cmd : reloadCmds) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }
            }
        }

        if (plugin.getConfig().getBoolean("override-bungee-config")) {
            String config = plugin.getConfig().getString("bungee-config");
            Gson json = new Gson();
            Main.config = json.fromJson(config, ConfigSave.class);
            Main.hasConfig = true;
            Main.plugin.getLogger().info("Obtained bungee config!");
        }

        new PapiExpansion().register();  // Register PlaceholderAPI expansion
    }

    @Override
    public void onDisable() {
        worldGroupInventoryManager.saveAllInventories();

        SQL.disconnect();  // Disconnect MySQL

        // Unregister plugin messaging channel
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);

        new PapiExpansion().unregister();  // Unregister PlaceholderAPI expansion
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
        mode.TriggersWarp = true;
        save.GameModes.add(mode);

        Gson json = new Gson();
        getLogger().info(json.toJson(save));
    }

}
