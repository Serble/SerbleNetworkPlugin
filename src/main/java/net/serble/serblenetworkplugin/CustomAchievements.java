package net.serble.serblenetworkplugin;

import net.serble.serblenetworkplugin.Schemas.Achievement;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class CustomAchievements implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.GRASS) return;
        AchievementsManager.GrantAchievementProgress(e.getPlayer(), Achievement.TOUCH_GRASS);
    }

}
