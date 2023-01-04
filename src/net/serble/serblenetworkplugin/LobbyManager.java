package net.serble.serblenetworkplugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class LobbyManager implements Listener {

    @EventHandler
    public void onHungerLevelChange(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();

        if (Main.plugin.getConfig().getStringList("lobbys").contains(p.getWorld().getName())) {
            e.setCancelled(true);
            p.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();

        if (Main.plugin.getConfig().getStringList("lobbys").contains(p.getWorld().getName()) &&
        !Main.plugin.getConfig().getStringList("allowdeathlobbys").contains(p.getWorld().getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (Main.sqlData.getAdminMode(e.getPlayer().getUniqueId())) return;
        if (Main.plugin.getConfig().getStringList("lobbys").contains(e.getPlayer().getWorld().getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (Main.sqlData.getAdminMode(e.getPlayer().getUniqueId())) return;
        if (Main.plugin.getConfig().getStringList("lobbys").contains(e.getPlayer().getWorld().getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (Main.sqlData.getAdminMode(e.getPlayer().getUniqueId())) return;
        if (Main.plugin.getConfig().getStringList("lobbys").contains(e.getPlayer().getWorld().getName())) {
            e.setCancelled(true);
        }
    }


}
