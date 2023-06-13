package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.Schemas.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class RankNickCmd implements CommandExecutor, TabCompleter {

    // public static HashMap<UUID, String> RankNicks = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        sender.sendMessage(Functions.translate("&cThis command has been disabled"));
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> result = new ArrayList<>();

        if (args.length > 1) return result;

        if (args.length == 0) {
            for (Rank rank : Main.config.Ranks) {
                if (!sender.hasPermission("serble.ranknick.self." + rank.Name.toLowerCase())) continue;
                result.add(rank.Name.replaceAll(" ", "~"));
            }
            return result;
        }

        for (Rank rank : Main.config.Ranks) {
            if (!sender.hasPermission("serble.ranknick.self." + rank.Name.toLowerCase())) continue;
            if (!rank.Name.toLowerCase().startsWith(args[0].toLowerCase())) continue;
            result.add(rank.Name.replaceAll(" ", "~"));
        }
        return result;
    }

}
