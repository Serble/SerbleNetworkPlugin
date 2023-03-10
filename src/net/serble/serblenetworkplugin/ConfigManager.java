package net.serble.serblenetworkplugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import net.serble.serblenetworkplugin.Schemas.ConfigSave;
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
            Main.plugin.getLogger().info("Obtained bungee config!");
        }
    }

}
