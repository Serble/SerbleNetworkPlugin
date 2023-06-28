package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.AchievementsManager;
import net.serble.serblenetworkplugin.NbtHandler;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import net.serble.serblenetworkplugin.Schemas.Config.GameModeMenuItem;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
            assert meta != null;
            if (!GameMode.equals("null")) {
                List<String> lore = new ArrayList<>();
                lore.add(Functions.translate("&7" + mi.Description));
                meta.setLore(lore);
            }

            meta.setDisplayName(Functions.translate("&a" + name));

            PersistentDataContainer data = meta.getPersistentDataContainer();
            NbtHandler.setTag(data, "gamemode", PersistentDataType.STRING, GameMode);

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
        ItemMeta meta = e.getCurrentItem().getItemMeta();
        if (meta == null) return;

        if (e.getView().getTitle().contains("Gamemodes")) e.setCancelled(true);

        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (!NbtHandler.hasTag(data, "gamemode", PersistentDataType.STRING)) return;
        String gameMode = NbtHandler.getTag(data, "gamemode", PersistentDataType.STRING);

        e.setCancelled(true);
        p.closeInventory();
        p.updateInventory();

        Bukkit.dispatchCommand(p, "proxyexecute play " + gameMode);
    }

}
