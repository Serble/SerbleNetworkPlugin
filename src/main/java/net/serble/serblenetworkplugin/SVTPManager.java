package net.serble.serblenetworkplugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class SVTPManager implements PluginMessageListener {

    public void onPluginMessageReceived(final String channel, final @NotNull Player player, final byte[] bytes) {
        if (!channel.equalsIgnoreCase("calcilator:svtp")) {
            return;
        }
        final ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        final String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("world")) {
            final String playerName = in.readUTF();
            final String worldName = in.readUTF();
            Player p = Bukkit.getPlayer(playerName);
            if (worldName.equals("null")) {
                return;
            }
            if (p == null) {
                Bukkit.getLogger().severe("Attempted to send a player that doesn't exist! (" + playerName + ")");
                return;
            }
            World w = Bukkit.getWorld(worldName);
            if (w == null) {
                p.sendMessage(Functions.translate("&cAttempted to send you to a world that doesn't exist!"));
                return;
            }
            if (p.getWorld().getName().equals(worldName)) {
                return;
            }
            p.teleport(w.getSpawnLocation());
            p.sendMessage(Functions.translate("&aYou have been sent"));
        }
    }

    public static void sendPlayer(final Player p, final String serverName, final String worldName) {
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("server");
            out.writeUTF(p.getName());
            out.writeUTF(serverName);
            out.writeUTF(worldName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        p.sendMessage(Functions.translate("&aSending you... &7(" + serverName + ", " + worldName + ")"));
        p.sendPluginMessage(Main.plugin, "calcilator:svtp", b.toByteArray());
    }

}
