package net.serble.serblenetworkplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class Chat implements Listener {

    @EventHandler
    public void onChatAsync(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();

        if (!Main.hasConfig) {
            // get config
            ConfigManager.RequestConfig(p);
        }

        if (!Main.hasConfig) return;

        String format = GetFormat(p);
        if (p.hasPermission("serble.chatcolours")) e.setMessage(Functions.translate(e.getMessage()));

        String finalMessage = Functions.translate(format);

        e.setFormat(finalMessage);
    }

    public static void FakeChat(Player p, String message) {
        if (!Main.hasConfig) {
            // get config
            ConfigManager.RequestConfig(p);
        }

        if (!Main.hasConfig) return;

        String format = GetFormat(p);

        if (p.hasPermission("serble.chatcolours")) message = Functions.translate(message);
        String finalMessage = Functions.translate(format);

        Bukkit.broadcastMessage(String.format(finalMessage, p.getDisplayName(), message));
    }

    private static String GetFormat(Player p) {
        // Get what rank should be displayed
        String rankDisplay = Functions.getPlayerRankDisplay(p);

        // Get what gamemode should be displayed
        List<String> worlds = Main.plugin.getConfig().getStringList("worlds");
        List<String> worldNames = Main.plugin.getConfig().getStringList("worldnames");

        String worldDisplay = Main.plugin.getConfig().getString("unknownworld");
        if (worldDisplay == null) {
            worldDisplay = "Unknown World";
        }

        for (int i = 0; i < worlds.size(); i++) {
            if (p.getWorld().getName().equals(worlds.get(i))) {
                worldDisplay = worldNames.get(i);
            }
        }

        String format = Main.config.ChatFormat;
        format = format.replaceAll("\\{world}", worldDisplay);
        format = format.replaceAll("\\{rank}", rankDisplay);
        format = format.replaceAll("\\{name}", "%s");
        format = format.replaceAll("\\{message}", "%s");
        return format;
    }


}
