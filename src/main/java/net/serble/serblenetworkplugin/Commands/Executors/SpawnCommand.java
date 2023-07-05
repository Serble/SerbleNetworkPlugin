package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnCommand extends SerbleCommand {

    private boolean canPlayerUseCommand(Player p) {
        if (p.hasPermission("serble.bypassspawn")) return true;

        List<String> worldsList = Main.plugin.getConfig().getStringList("allowspawnworlds");
        boolean isBlacklist = Main.plugin.getConfig().getBoolean("allowspawnworldsisblacklist");

        if (isBlacklist) {
            return !worldsList.contains(p.getWorld().getName());
        } else {
            return worldsList.contains(p.getWorld().getName());
        }
    }

    @Override
    public void execute(SlashCommand cmd) {
        Player p = cmd.getPlayerExecutor();
        if (!canPlayerUseCommand(p)) {
            cmd.sendError("You can't do that here!");
            return;
        }
        p.teleport(p.getWorld().getSpawnLocation());
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return EMPTY_TAB_COMPLETE;
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return PLAYER_SENDER;
    }
}
