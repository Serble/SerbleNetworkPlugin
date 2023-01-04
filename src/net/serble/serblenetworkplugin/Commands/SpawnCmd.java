package net.serble.serblenetworkplugin.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can't do that");
            return true;
        }
        Player p = (Player) sender;
        p.teleport(p.getWorld().getSpawnLocation());
        return true;
    }

}
