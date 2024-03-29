package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.AchievementsManager;
import net.serble.serblenetworkplugin.Cache.MoneyCacheManager;
import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.GameProfileUtils;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.Config.StoreItem;
import org.bukkit.Bukkit;
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

public class StoreCommand extends SerbleCommand implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (!e.getView().getTitle().contains("Store")) return;
        e.setCancelled(true);

        p.closeInventory();
        p.updateInventory();

        ArrayList<StoreItem> storeItems = Main.config.StoreItems;

        for (StoreItem item : storeItems) {
            if (e.getSlot() != item.Slot) continue;
            // found the item
            // now run the command if they have the money
            int balance = MoneyCacheManager.getMoney(GameProfileUtils.getPlayerUuid(p));
            if (balance < item.Cost) {
                // they don't have enough
                p.sendMessage(Functions.translate("&cYou don't have enough money! Balance: " + balance));
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

    @Override
    public void execute(SlashCommand cmd) {
        Player p = cmd.getPlayerExecutor();
        Inventory store = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Store");

        ArrayList<StoreItem> StoreItems = Main.config.StoreItems;
        int balance = MoneyCacheManager.getMoney(GameProfileUtils.getPlayerUuid(p));

        for (StoreItem storeItem : StoreItems) {
            if (!p.hasPermission(storeItem.Permission)) continue;

            Material material = Material.getMaterial(storeItem.Material);
            String name = Functions.translate("&r&a" + storeItem.Name);
            Integer slot = storeItem.Slot;
            Integer price = storeItem.Cost;

            assert material != null;
            ItemStack stack = new ItemStack(material, 1);
            ItemMeta meta = stack.getItemMeta();

            List<String> lore = new ArrayList<>();
            lore.add(Functions.translate("&r&6" + price + " coins"));
            if (balance >= price) {
                lore.add(Functions.translate("&aClick to buy"));
            } else {
                lore.add(Functions.translate("&cYou cannot afford this"));
            }
            assert meta != null;
            meta.setLore(lore);

            meta.setDisplayName(Functions.translate(name));
            stack.setItemMeta(meta);

            store.setItem(slot, stack);
        }

        p.openInventory(store);
        AchievementsManager.GrantAchievementProgress(p, Achievement.GO_SHOPPING);
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
