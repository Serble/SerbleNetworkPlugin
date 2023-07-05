package net.serble.serblenetworkplugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import net.serble.serblenetworkplugin.Schemas.Config.ConfigSave;
import net.serble.serblenetworkplugin.Schemas.Config.GameMode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.LinkedList;
import java.util.Queue;

public class ConfigManager implements PluginMessageListener {
    private static Queue<Runnable> queuedTasks = new LinkedList<>();
    private static boolean configRequested = false;

    public static void requestConfig(Player p) {
        requestConfig(p, false);
    }

    public static void requestConfig(Player p, boolean ignoreCache) {
        if (configRequested && !ignoreCache) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("config");
        out.writeUTF(p.getName());

        Main.plugin.getLogger().info("Sending request for config...");
        p.sendPluginMessage(Main.plugin, "serble:serble", out.toByteArray());
        configRequested = true;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {

        if (!channel.equals("serble:serble")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        if (subChannel.equals("config")) {
            String config = in.readUTF();
            Gson json = new Gson();
            Main.config = json.fromJson(config, ConfigSave.class);
            Main.hasConfig = true;
            Main.plugin.getLogger().info("Obtained bungee config! ChatFormat: " + Main.config.ChatFormat);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("serble.staff")) {
                    DebugManager.getInstance().debug(p, "&aObtained bungee config!");
                }
            }
            for (GameMode mode : Main.config.GameModes) {
                Main.plugin.getLogger().info("Discovered GameMode: " + mode.Name);
            }
            executeTasks();
        }
    }

    private static void executeTasks() {
        if (queuedTasks == null) {
            return;
        }
        while (!queuedTasks.isEmpty()) {
            queuedTasks.poll().run();
        }
        queuedTasks = null;
    }

    public static void runTaskAfterConfig(Runnable runnable) {
        if (queuedTasks == null) {
            runnable.run();
        } else {
            queuedTasks.add(runnable);
        }
    }

}
