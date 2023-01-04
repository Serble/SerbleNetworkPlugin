package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Schemas.Rank;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RankNickCmd implements CommandExecutor, TabCompleter {

    // public static HashMap<UUID, String> RankNicks = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("serble.ranknick.self") && !sender.hasPermission("serble.ranknick.others")) {
            sender.sendMessage(Functions.translate("&4You do not have permission!"));
            return true;
        }

        if (args.length == 1) {

            if (!(sender instanceof Player)) {
                sender.sendMessage("You cannot do that!");
                return true;
            }

            String rank = args[0];

            if (!sender.hasPermission("serble.ranknick.self." + rank)) {
                sender.sendMessage(Functions.translate("&4You do not have permission to nick as that!"));
                return true;
            }

            rank = rank.replace('~', ' ');

            UUID uuid = ((Player) sender).getUniqueId();

            Main.sqlData.setRankNick(uuid, rank);

            sender.sendMessage(Functions.translate("&aUser has been successfully nicked!"));
            return true;
        } else if (args.length == 2) {

            if (!sender.hasPermission("serble.ranknick.others")) {
                sender.sendMessage(Functions.translate("&4You do not have permission!"));
                return true;
            }
            Player target;
            try {
                target = Bukkit.getPlayer(args[0]);
            } catch (Exception e) {
                sender.sendMessage(Functions.translate("&4Invalid Player"));
                return true;
            }
            String rank = args[1];

            if (!sender.hasPermission("serble.ranknick.others." + rank)) {
                sender.sendMessage(Functions.translate("&4You do not have permission to nick people as that!"));
                return true;
            }

            rank = rank.replace('~', ' ');

            Main.sqlData.setRankNick(target.getUniqueId(), rank);

            sender.sendMessage(Functions.translate("&aUser has been successfully nicked!"));
            return true;

        } else {
            sender.sendMessage(Functions.translate("&4Usage: /ranknick <PLAYER> <RANK>"));
            sender.sendMessage(Functions.translate("&4Usage: /ranknick <RANK>"));
            sender.sendMessage(Functions.translate("&4Please note that if you want to add a space to the rank name use '~'"));
            return true;
        }

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
