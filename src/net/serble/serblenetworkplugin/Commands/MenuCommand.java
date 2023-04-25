package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.AchievementsManager;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import net.serble.serblenetworkplugin.Schemas.GameModeMenuItem;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import org.bukkit.Bukkit;
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

public class MenuCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        Inventory Gamemodes = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Gamemodes");

        for (int i = 0; i < Main.config.GameModeMenuItems.size(); i++) {
            GameModeMenuItem mi = Main.config.GameModeMenuItems.get(i);
            Material material = Material.getMaterial(mi.Material);
            String GameMode = mi.GameMode;
            String name = mi.Name;

            assert material != null;
            ItemStack stack = new ItemStack(material, 1);
            ItemMeta meta = stack.getItemMeta();
            if (!GameMode.equals("null")) {
                List<String> lore = new ArrayList<>();
                lore.add("GameMode:");
                lore.add(GameMode);
                meta.setLore(lore);
            }

            meta.setDisplayName(Functions.translate(name));
            stack.setItemMeta(meta);

            Gamemodes.addItem(stack);
        }

        p.openInventory(Gamemodes);
        AchievementsManager.GrantAchievementProgress(p, Achievement.OPEN_MENU);

        return false;
    }


    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getItemMeta() == null) return;
        List<String> lore = e.getCurrentItem().getItemMeta().getLore();

        if (e.getView().getTitle().contains("Gamemodes")) e.setCancelled(true);

        if (lore == null) return;
        if (lore.size() != 2) return;
        if (!lore.get(0).equals("GameMode:")) return;

        e.setCancelled(true);
        p.closeInventory();
        p.updateInventory();

        Bukkit.dispatchCommand(p, "play " + lore.get(1));
    }

}
