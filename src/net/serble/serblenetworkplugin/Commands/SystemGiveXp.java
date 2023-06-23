package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.GameProfileUtils;
import net.serble.serblenetworkplugin.ExperienceManager;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.MenuItemManager;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.UnprocessedCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SystemGiveXp implements CommandExecutor {

    public static void giveXp(Player p, int amount, String reason) {
        ExperienceManager.addSerbleXp(GameProfileUtils.getPlayerUuid(p), amount);

        // Message in chat
        p.sendMessage(Functions.translate("&9+ " + amount + " XP (" + reason + ")"));

        // If they have their level on their XP bar then change it
        if (MenuItemManager.shouldNotGetItems(p)) return;
        ExperienceManager.setPlayerExperience(p);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SlashCommand cmd = new UnprocessedCommand(sender, args)
                .withPermission("serble.sysgivexp")
                .withUsage("&cUsage: /sysgivexp <PLAYER> <AMOUNT> <REASON>")
                .process();

        if (!cmd.isAllowed()) {
            return true;
        }

        Integer amount = cmd.getArg(1) == null ? null : cmd.getArg(1).getInteger();
        if (amount == null) {
            cmd.sendUsage("Invalid amount");
            return true;
        }

        List<Player> players = cmd.getArg(0) == null ? null : cmd.getArg(0).getPlayerList();
        if (players == null) {
            cmd.sendUsage("Invalid player");
            return true;
        }

        String reason = cmd.combineArgs(2);
        for (Player p : players) giveXp(p, amount, reason);

        if (sender instanceof Player) {
            sender.sendMessage(Functions.translate("&aGave " + args[0] + " " + amount + " XP"));
        }

        return true;
    }

}
