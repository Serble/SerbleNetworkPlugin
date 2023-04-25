package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.AchievementsManager;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantAchievementProgressCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("serble.grantachievement")) {
            sender.sendMessage(Functions.translate("&4You do not have permission!"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Functions.translate("&4Usage: /grantachievementprogress <PLAYER> <ACHIEVEMENT> <PROGRESS>"));
            return true;
        }

        Achievement achievement;
        try {
            achievement = Achievement.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Functions.translate("&4Invalid Amount. Usage: /grantachievementprogress <PLAYER> <ACHIEVEMENT> <PROGRESS>"));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (Exception e) {
            sender.sendMessage(Functions.translate("&4Invalid Amount. Usage: /grantachievementprogress <PLAYER> <ACHIEVEMENT> <PROGRESS>"));
            return true;
        }

        Player p;
        try {
            p = Bukkit.getPlayer(args[0]);
        } catch (Exception e) {
            sender.sendMessage(Functions.translate("&4Invalid Player. Usage: /grantachievementprogress <PLAYER> <ACHIEVEMENT> <PROGRESS>"));
            return true;
        }

        assert p != null;
        AchievementsManager.GrantAchievementProgress(p, achievement, amount);

        return true;
    }

}
