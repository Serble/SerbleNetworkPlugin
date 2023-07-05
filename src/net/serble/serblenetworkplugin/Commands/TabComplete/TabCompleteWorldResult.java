package net.serble.serblenetworkplugin.Commands.TabComplete;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteWorldResult implements TabCompleteSlotResult {
    @Override
    public List<String> get() {
        List<String> worlds = new ArrayList<>();
        Bukkit.getWorlds().forEach(world -> worlds.add(world.getName()));
        return worlds;
    }
}
