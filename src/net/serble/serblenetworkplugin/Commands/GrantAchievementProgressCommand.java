package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.AchievementsManager;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.UnprocessedCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantAchievementProgressCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SlashCommand cmd = new UnprocessedCommand(sender, args)
                .withPermission("serble.grantachievement")
                .withUsage("/grantachievementprogress <PLAYER> <ACHIEVEMENT> <PROGRESS>")
                .process();

        Achievement achievement;
        try {
            achievement = Achievement.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            cmd.sendUsage("Invalid achievement");
            return true;
        }

        Integer amount = cmd.getArg(2) == null ? null : cmd.getArg(2).getInteger();
        if (amount == null) {
            cmd.sendUsage("Invalid progress amount");
            return true;
        }

        Player p = cmd.getArg(0) == null ? null : cmd.getArg(0).getPlayer();
        if (p == null) {
            cmd.sendUsage("Invalid player");
            return true;
        }

        AchievementsManager.GrantAchievementProgress(p, achievement, amount);
        return true;
    }

}
