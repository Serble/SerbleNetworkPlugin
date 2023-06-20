package net.serble.serblenetworkplugin.Commands;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.GameProfileUtils;
import net.serble.serblenetworkplugin.MoneyCacheManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SystemGiveMoney implements CommandExecutor {

    public static void GiveMoney(Player p, int amount, String reason) {
        MoneyCacheManager.addMoney(GameProfileUtils.getPlayerUuid(p), amount);

        // Message in chat
        p.sendMessage(Functions.translate("&6+ " + amount + " coins (" + reason + ")"));

        // Action bar message
        BaseComponent[] message2 = {
                new TextComponent("+ " + amount + " coins (" + reason + ")"),
        };
        message2[0].setColor(ChatColor.GOLD.asBungee());
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, message2);

        // Sound
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("serble.sysgivemoney")) {
            sender.sendMessage(Functions.translate("&4You do not have permission!"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Functions.translate("&4Usage: /sysgivemoney <PLAYER> <AMOUNT> <REASON>"));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception e) {
            sender.sendMessage(Functions.translate("&4Invalid Amount. Usage: /sysgivemoney <PLAYER> <AMOUNT> <REASON>"));
            return true;
        }

        Player p;
        try {
            p = Bukkit.getPlayer(args[0]);
        } catch (Exception e) {
            sender.sendMessage(Functions.translate("&4Invalid Player. Usage: /sysgivemoney <PLAYER> <AMOUNT> <REASON>"));
            return true;
        }

        StringBuilder reason = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }
        reason.deleteCharAt(reason.length()-1);

        assert p != null;
        GiveMoney(p, amount, reason.toString());

        if (sender instanceof Player) {
            sender.sendMessage(Functions.translate("&aGave " + p.getName() + " " + amount + " coins"));
        }

        return true;
    }

}
