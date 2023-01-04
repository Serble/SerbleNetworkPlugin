package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Schemas.GameMode;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.SVTPManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can do this");
            return true;
        }

        if (args.length == 0) {
            Bukkit.dispatchCommand(sender, "lob");
            return true;
        }

        for (GameMode gm : Main.config.GameModes) {
            if (!gm.Name.equalsIgnoreCase(args[0])) continue;
            if (!sender.hasPermission(gm.Permission)) {
                sender.sendMessage(Functions.translate("&4You do not have permission!"));
                return true;
            }

            // Send them
            SVTPManager.SendPlayer((Player) sender, gm.Server, gm.World);
            return true;
        }

        Bukkit.dispatchCommand(sender, "lob");
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<>();

        if (args.length > 1) return results;

        if (args.length == 0) {
            for (GameMode gm : Main.config.GameModes) {
                results.add(gm.Name);
            }
            return results;
        }

        for (GameMode gm : Main.config.GameModes) {
            if (!sender.hasPermission(gm.Permission)) continue;
            if (!gm.Name.startsWith(args[0])) continue;
            results.add(gm.Name);
        }

        return results;
    }
}
