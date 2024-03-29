package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.AchievementsManager;
import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.NbtHandler;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.Config.GameMode;
import net.serble.serblenetworkplugin.Schemas.Config.GameModeMenuItem;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class MenuCommand extends SerbleCommand implements Listener {

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

    @Override
    public void execute(SlashCommand cmd) {
        Player p = cmd.getPlayerExecutor();

        if (!Main.hasConfig) {
            Functions.sendNoConfigMessage(p);
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Gamemodes");

        for (int i = 0; i < Main.config.GameModeMenuItems.size(); i++) {
            GameModeMenuItem mi = Main.config.GameModeMenuItems.get(i);
            Material material = Material.getMaterial(mi.Material);
            String gameModeString = mi.GameMode;
            String name = mi.Name;

            GameMode mode = Main.config.GameModes
                    .stream()
                    .filter(gm -> gm.Name.equalsIgnoreCase(gameModeString))
                    .findFirst()
                    .orElse(null);
            if (mode != null && !p.hasPermission(mode.Permission)) continue;

            assert material != null;
            ItemStack stack = new ItemStack(material, 1);
            ItemMeta meta = stack.getItemMeta();
            assert meta != null;
            if (!gameModeString.equals("null")) {
                List<String> lore = new ArrayList<>();
                lore.add(Functions.translate("&7" + mi.Description));
                meta.setLore(lore);
            }

            meta.setDisplayName(Functions.translate("&a" + name));

            PersistentDataContainer data = meta.getPersistentDataContainer();
            NbtHandler.setTag(data, "gamemode", PersistentDataType.STRING, gameModeString);

            stack.setItemMeta(meta);
            inventory.addItem(stack);
        }

        p.openInventory(inventory);
        AchievementsManager.GrantAchievementProgress(p, Achievement.OPEN_MENU);
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return EMPTY_TAB_COMPLETE;
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return getArrayOfSenders(CommandSenderType.Player);
    }
}
