package net.serble.serblenetworkplugin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class LobbyManager implements Listener {

    private boolean isLobby(Player p, boolean excludeAdminMode) {
        if (excludeAdminMode && AdminModeCacheHandler.isAdminMode(p.getUniqueId())) return false;
        return Main.plugin.getConfig().getStringList("lobbys").contains(p.getWorld().getName());
    }

    private boolean isLobby(Player p) {
        return isLobby(p, false);
    }

    @EventHandler
    public void onHungerLevelChange(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();

        if (isLobby(p)) {
            e.setCancelled(true);
            p.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();

        if (isLobby(p) &&
        !Main.plugin.getConfig().getStringList("allowdeathlobbys").contains(p.getWorld().getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (isLobby(e.getPlayer(), true)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (isLobby(e.getPlayer(), true)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (isLobby(e.getPlayer(), true)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCropTrample(PlayerInteractEvent e) {
        if (isLobby(e.getPlayer())) {
            if (e.getAction() == Action.PHYSICAL) {
                e.setCancelled(true);
            }
        }
    }

    // make slime blocks launch players in lobby
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Material blockBelow = player.getLocation().subtract(0, 1, 0).getBlock().getType();

        if (blockBelow == Material.SLIME_BLOCK) {
            Vector direction = player.getLocation().getDirection().multiply(2).setY(1);
            player.setVelocity(direction);
        }
    }


}
