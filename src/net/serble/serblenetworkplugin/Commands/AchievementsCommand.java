package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.AchievementsManager;
import net.serble.serblenetworkplugin.GameProfileUtils;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.UnprocessedCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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

public class AchievementsCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SlashCommand cmd = new UnprocessedCommand(sender, args)
                .withValidSenders(CommandSenderType.Player)
                .process();
        if (!cmd.isAllowed()) {
            return true;
        }

        Player p = (Player) sender;
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
        return true;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getView().getTitle().contains("Achievements")) e.setCancelled(true);
    }

}
