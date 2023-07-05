package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.AchievementsManager;
import net.serble.serblenetworkplugin.GameProfileUtils;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AchievementsCommand extends SerbleCommand implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getView().getTitle().contains("Achievements")) e.setCancelled(true);
    }

    @Override
    public void execute(SlashCommand cmd) {
        Player p = cmd.getPlayerExecutor();
        UUID profile = GameProfileUtils.getPlayerUuid(p);
        Inventory inv = p.getServer().createInventory(null, 9 * 6, "Achievements");

        List<Achievement> completed = AchievementsManager.getPlayerCompletedAchievements(GameProfileUtils.getPlayerUuid(p));
        List<Achievement> uncompleted = AchievementsManager.getPlayerUncompletedAchievements(GameProfileUtils.getPlayerUuid(p));

        for (int i = 0; i < completed.size(); i++) {
            Achievement a = completed.get(i);

            ItemStack item = new ItemStack(Material.GOLD_BLOCK);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.GOLD + AchievementsManager.AchievementNames.get(a));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN + AchievementsManager.AchievementDescriptions.get(a));
            lore.add(ChatColor.RED + "Completed");
            meta.setLore(lore);

            item.setItemMeta(meta);
            inv.setItem(i, item);
        }

        for (int i = 0; i < uncompleted.size(); i++) {
            Achievement a = uncompleted.get(i);

            ItemStack item = new ItemStack(Material.COAL_BLOCK);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.GRAY + AchievementsManager.AchievementNames.get(a));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN + AchievementsManager.AchievementDescriptions.get(a));
            lore.add(ChatColor.RED + "(" + AchievementsManager.getPlayerProgress(profile, a) + "/" + AchievementsManager.AchievementProgressLimits.get(a) + ")");
            meta.setLore(lore);

            item.setItemMeta(meta);
            inv.setItem(i + completed.size(), item);
        }

        p.openInventory(inv);
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return null;
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return PLAYER_SENDER;
    }
}
