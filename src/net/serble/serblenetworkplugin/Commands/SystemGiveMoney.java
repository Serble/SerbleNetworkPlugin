package net.serble.serblenetworkplugin.Commands;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.GameProfileUtils;
import net.serble.serblenetworkplugin.MoneyCacheManager;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommandArgument;
import net.serble.serblenetworkplugin.Schemas.UnprocessedCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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
        SlashCommand cmd = new UnprocessedCommand(sender, args)
                .withPermission("serble.sysgivemoney")
                .withUsage("/sysgivemoney <PLAYER> <AMOUNT> <REASON>")
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
            cmd.sendUsage("Invalid player argument");
            return true;
        }

        String reason = cmd.combineArgs(2);
        for (Player p : players) GiveMoney(p, amount, reason);

        if (sender instanceof Player) {
            sender.sendMessage(Functions.translate("&aGave " + args[0] + " " + amount + " coins"));
        }
        return true;
    }

}
