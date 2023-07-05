package net.serble.serblenetworkplugin.Commands.TabComplete;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;

import java.util.List;

public class TabCompletePlayerResult implements TabCompleteSlotResult {
    @Override
    public List<String> get() {
        return List.of(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toArray(String[]::new));
    }
}
