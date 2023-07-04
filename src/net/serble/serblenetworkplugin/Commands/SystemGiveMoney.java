package net.serble.serblenetworkplugin.Commands;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.GameProfileUtils;
import net.serble.serblenetworkplugin.MoneyCacheManager;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SerbleCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletePlayerResult;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletionBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class SystemGiveMoney extends SerbleCommand {

    public static void giveMoney(Player p, int amount, String reason) {
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
    public void execute(SlashCommand cmd) {  // /sysgivemoney <player> <amount> [reason]
        Integer amount = cmd.getArg(1) == null ? null : cmd.getArg(1).getInteger();
        if (amount == null) {
            cmd.sendUsage("Invalid amount");
            return;
        }

        List<Player> players = cmd.getArg(0) == null ? null : cmd.getArg(0).getPlayerList();
        if (players == null) {
            cmd.sendUsage("Invalid player argument");
            return;
        }

        String reason = cmd.combineArgs(2);
        for (Player p : players) giveMoney(p, amount, reason);

        if (cmd.getSenderType() == CommandSenderType.Player) {
            cmd.send("&aGave " + cmd.getArg(0).getText() + " " + amount + " coins");
        }
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return new TabCompletionBuilder(cmd.getArgs())
                .setCase(new TabCompletePlayerResult());
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return ALL_SENDERS;
    }
}
