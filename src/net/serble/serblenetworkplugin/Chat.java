package net.serble.serblenetworkplugin;

import net.serble.serblenetworkplugin.Schemas.Achievement;
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

        Bukkit.getScheduler().callSyncMethod(Main.plugin, () -> {
            AchievementsManager.GrantAchievementProgress(p, Achievement.I_HAVE_A_VOICE);
            return null;
        });

        if (!Main.hasConfig) {
            // get config
            ConfigManager.requestConfig(p);
            e.setCancelled(true);
            ConfigManager.runTaskAfterConfig(() -> fakeChat(p, e.getMessage()));
            return;
        }

        String format = getFormat(p);
        if (p.hasPermission("serble.chatcolours")) e.setMessage(Functions.translate(e.getMessage()));

        String finalMessage = Functions.translate(format);

        e.setFormat(finalMessage);
    }

    public static void fakeChat(Player p, String message) {
        if (!Main.hasConfig) {
            // get config
            ConfigManager.requestConfig(p);
            final String finalMessage = message;
            ConfigManager.runTaskAfterConfig(() -> fakeChat(p, finalMessage));
            return;
        }

        String format = getFormat(p);

        if (p.hasPermission("serble.chatcolours")) message = Functions.translate(message);
        String finalMessage = Functions.translate(format);

        Bukkit.broadcastMessage(String.format(finalMessage, p.getDisplayName(), message));
    }

    private static String getFormat(Player p) {
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
        format = format.replaceAll("\\{level}", Integer.toString(ExperienceManager.getLevel(p)));
        format = format.replaceAll("\\{name}", "%s");
        format = format.replaceAll("\\{message}", "%s");
        return format;
    }


}
