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

public class ConfigManager implements PluginMessageListener {

    public static void RequestConfig(Player p) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("config");
        out.writeUTF(p.getName());

        Main.plugin.getLogger().info("Sending request for config...");
        p.sendPluginMessage(Main.plugin, "serble:serble", out.toByteArray());
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
        }
    }

}
