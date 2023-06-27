package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.*;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import net.serble.serblenetworkplugin.Schemas.Config.StoreItem;
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
import java.util.Objects;

public class StoreCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        Inventory Store = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Store");

        ArrayList<StoreItem> StoreItems = Main.config.StoreItems;

        for (StoreItem storeItem : StoreItems) {
            if (!p.hasPermission(storeItem.Permission)) continue;

            Material material = Material.getMaterial(storeItem.Material);
            String name = storeItem.Name;
            Integer slot = storeItem.Slot;
            Integer price = storeItem.Cost;

            assert material != null;
            ItemStack stack = new ItemStack(material, 1);
            ItemMeta meta = stack.getItemMeta();

            List<String> lore = new ArrayList<>();
            lore.add("Price: " + price);
            assert meta != null;
            meta.setLore(lore);

            meta.setDisplayName(Functions.translate(name));
            stack.setItemMeta(meta);

            Store.setItem(slot, stack);
        }

        p.openInventory(Store);
        AchievementsManager.GrantAchievementProgress(p, Achievement.GO_SHOPPING);

        return false;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (!e.getView().getTitle().contains("Store")) return;
        e.setCancelled(true);

        p.closeInventory();
        p.updateInventory();

        ArrayList<StoreItem> StoreItems = Main.config.StoreItems;

        for (StoreItem item : StoreItems) {
            if (e.getSlot() != item.Slot) continue;
            if (!Functions.translate(item.Name).equals(Functions.translate(Objects.requireNonNull(Objects.requireNonNull(e.getCurrentItem()).getItemMeta()).getDisplayName()))) continue;
            // found the item
            // now run the command if they have the money
            int balance = MoneyCacheManager.getMoney(GameProfileUtils.getPlayerUuid(p));
            if (balance < item.Cost) {
                // they don't have enough
                p.sendMessage(Functions.translate("&4You don't have enough money! Balance: " + balance));
                return;
            }

            MoneyCacheManager.addMoney(GameProfileUtils.getPlayerUuid(p), -item.Cost);

            for (String cmd : item.Commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", p.getName()));
            }

            p.sendMessage(Functions.translate("&aPurchased &l" + item.Name + "&r&a for &6" + item.Cost + "&a coins!"));
            return;
        }

    }

}
