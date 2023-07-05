package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.GameProfileUtils;
import net.serble.serblenetworkplugin.Cache.MoneyCacheManager;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommandArgument;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompleteEnumResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletePlayerResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class MoneyCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        //       0   1    2
        // money balance [USER]
        // money set USER AMOUNT
        // money add USER AMOUNT

        if (cmd.getArgs().length == 0) {
            cmd.sendUsage();
            return;
        }

        SlashCommandArgument firstArg = cmd.getArgIgnoreNull(0);
        if (firstArg.equalsIgnoreCase("set")) {
            OfflinePlayer p = cmd.getArgIgnoreNull(1).getOfflinePlayer();
            if (p == null) {
                cmd.sendUsage("Invalid player");
                return;
            }
            UUID uuid = GameProfileUtils.getPlayerUuid(p.getUniqueId());

            Integer value = cmd.getArgIgnoreNull(2).getInteger();
            if (value == null) {
                cmd.sendUsage("Invalid number");
                return;
            }

            MoneyCacheManager.setMoney(uuid, value);
            cmd.send(String.format("&aSet %s's balance to &7" + value, p.getName()));
            return;
        }

        if (firstArg.equalsIgnoreCase("add")) {
            OfflinePlayer p = cmd.getArgIgnoreNull(1).getOfflinePlayer();
            if (p == null) {
                cmd.sendUsage("Invalid player");
                return;
            }
            UUID uuid = GameProfileUtils.getPlayerUuid(p.getUniqueId());

            Integer value = cmd.getArgIgnoreNull(2).getInteger();
            if (value == null) {
                cmd.sendUsage("Invalid number");
                return;
            }

            MoneyCacheManager.addMoney(uuid, value);
            cmd.send(String.format("&aAdded &7" + value + "&a to %s's balance ", p.getName()));
            return;
        }

        if (firstArg.equalsIgnoreCase("balance") || firstArg.equalsIgnoreCase("bal")) {
            if (cmd.getSenderType() == CommandSenderType.Player && cmd.getArgs().length == 1) {
                int bal = MoneyCacheManager.getMoney(GameProfileUtils.getPlayerUuid(cmd.getPlayerExecutor()));
                cmd.send("&aBalance: &7" + bal);
                return;
            }

            OfflinePlayer p = cmd.getArgIgnoreNull(1).getOfflinePlayer();
            if (p == null) {
                cmd.sendUsage("Invalid player");
                return;
            }
            UUID uuid = GameProfileUtils.getPlayerUuid(p.getUniqueId());

            int bal = MoneyCacheManager.getMoney(uuid);
            cmd.send(String.format("&aBalance of %s: &7" + bal, p.getName()));
            return;
        }
        cmd.sendUsage();
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return new TabCompletionBuilder(cmd.getArgs())
                .setCase(new TabCompleteEnumResult("set", "add", "balance", "bal"))
                .setCase(new TabCompletePlayerResult(), "set")
                .setCase(new TabCompletePlayerResult(), "add")
                .setCase(new TabCompletePlayerResult(), "balance")
                .setCase(new TabCompletePlayerResult(), "bal");
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return ALL_SENDERS;
    }
}
