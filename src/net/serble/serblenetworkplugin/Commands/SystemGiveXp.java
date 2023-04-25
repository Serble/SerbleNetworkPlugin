package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.ExperienceManager;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.MenuItemManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SystemGiveXp implements CommandExecutor {

    public static void GiveXp(Player p, int amount, String reason) {
        Main.sqlData.addXp(p.getUniqueId(), amount);

        // Message in chat
        p.sendMessage(Functions.translate("&9+ " + amount + " XP (" + reason + ")"));

        // If they have their level on their XP bar then change it
        if (MenuItemManager.shouldNotGetItems(p)) return;
        ExperienceManager.setPlayerExperience(p);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("serble.sysgivexp")) {
            sender.sendMessage(Functions.translate("&4You do not have permission!"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Functions.translate("&4Usage: /sysgivexp <PLAYER> <AMOUNT> <REASON>"));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception e) {
            sender.sendMessage(Functions.translate("&4Invalid Amount. Usage: /sysgivexp <PLAYER> <AMOUNT> <REASON>"));
            return true;
        }

        Player p;
        try {
            p = Bukkit.getPlayer(args[0]);
        } catch (Exception e) {
            sender.sendMessage(Functions.translate("&4Invalid Player. Usage: /sysgivexp <PLAYER> <AMOUNT> <REASON>"));
            return true;
        }

        StringBuilder reason = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }
        reason.deleteCharAt(reason.length()-1);

        assert p != null;
        GiveXp(p, amount, reason.toString());

        if (sender instanceof Player) {
            sender.sendMessage(Functions.translate("&aGave " + p.getName() + " " + amount + " XP"));
        }

        return true;
    }

}
