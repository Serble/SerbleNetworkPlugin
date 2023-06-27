package net.serble.serblenetworkplugin;

import net.serble.serblenetworkplugin.Schemas.Config.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuItemManager implements Listener {

    public static void GiveMenuItems(Player p) {

        if (!Main.hasConfig) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    GiveMenuItems(p);
                }

            }.runTaskLater(Main.plugin, 90L);
            return;
        }

        ArrayList<ItemStack> items = new ArrayList<>();
        ArrayList<Integer> slots = new ArrayList<>();

        // get items
        for (int i = 0; i < Main.config.MenuItems.size(); i++) {
            MenuItem mi = Main.config.MenuItems.get(i);
            Material material = Material.getMaterial(mi.Material);
            int slot = mi.Slot;
            ArrayList<String> cmds = mi.Commands;
            String name = mi.Name;

            assert material != null;
            ItemStack stack = new ItemStack(material, 1);
            ItemMeta meta = stack.getItemMeta();
            if (!cmds.get(0).equals("null")) {
                List<String> lore = new ArrayList<>();
                lore.add("Commands:");
                lore.addAll(cmds);
                assert meta != null;
                meta.setLore(lore);
            }

            String rank = Functions.getPlayerRankDisplayB(p, true);
            name = name.replace("{rank}", rank);

            assert meta != null;
            meta.setDisplayName(Functions.translate(name));
            stack.setItemMeta(meta);

            items.add(stack);
            slots.add(slot);
        }

        // giv da items
        p.getInventory().clear();
        for (int i = 0; i < items.size(); i++) {
            p.getInventory().setItem(slots.get(i), items.get(i));
        }

        // Set XP bar to the person's experience level
        ExperienceManager.setPlayerExperience(p);
    }

    public static boolean shouldNotGetItems(Player p) {
        if (!Main.plugin.getConfig().getStringList("lobbys").contains(p.getWorld().getName())) return true;
        return AdminModeCacheHandler.isAdminMode(p.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (!Main.plugin.getConfig().getBoolean("disabletp")) {
            World lobby = Bukkit.getWorld(Objects.requireNonNull(Main.plugin.getConfig().getString("mainlobby")));
            assert lobby != null;
            p.teleport(lobby.getSpawnLocation());
        }

        if (shouldNotGetItems(p)) return;
        GiveMenuItems(p);

        if (!Main.plugin.getConfig().getStringList("noflylobbys").contains(p.getWorld().getName())) {
            p.setAllowFlight(p.hasPermission("serble.lobbyfly"));
        } else {
            p.setAllowFlight(false);
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();

        if (shouldNotGetItems(p)) return;
        GiveMenuItems(p);

        if (!Main.plugin.getConfig().getStringList("noflylobbys").contains(p.getWorld().getName())) {
            p.setAllowFlight(p.hasPermission("serble.lobbyfly"));
        } else {
            p.setAllowFlight(false);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!Main.hasConfig) {
            ConfigManager.RequestConfig(e.getPlayer());
            e.setCancelled(true);
            return;
        }

        ItemStack item = e.getItem();
        if (item == null) return;

        List<String> lore = Objects.requireNonNull(item.getItemMeta()).getLore();
        if (lore == null) return;

        if (lore.size() != 2) return;
        if (!lore.get(0).equals("Commands:")) return;

        if (lore.get(1).equals("none")) return;

        for (String li : lore) {
            if (li.equals("Commands:")) continue;
            Bukkit.dispatchCommand(e.getPlayer(), li);
        }

    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (AdminModeCacheHandler.isAdminMode(e.getWhoClicked().getUniqueId())) return;
        if (e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) return;
        if (Main.plugin.getConfig().getStringList("lobbys").contains(e.getWhoClicked().getWorld().getName())) {
            e.setCancelled(true);
        }
    }

}
