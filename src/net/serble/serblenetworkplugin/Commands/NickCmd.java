package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.NicknameManager;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.Config.Rank;
import net.serble.serblenetworkplugin.Schemas.SerbleCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompleteEnumResult;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletePlayerResult;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NickCmd extends SerbleCommand {

    // /nick <PLAYER> <NAME> <RANK> <SKIN>
    @Override
    public void execute(SlashCommand cmd) {
        CommandSender sender = cmd.getExecutor();

        if (!sender.hasPermission("serble.nick.self") && !sender.hasPermission("serble.nick.others")) {
            sender.sendMessage(Functions.translate("&cYou do not have permission!"));
            return;
        }

        int argLength = cmd.getArgs().length;
        if (argLength > 0) {
            if (!sender.hasPermission("serble.nick.others")) {
                sender.sendMessage(Functions.translate("&cYou do not have permission!"));
                return;
            }

            Player target = cmd.getArgIgnoreNull(0).getPlayer();
            if (target == null) {
                cmd.sendUsage("Invalid player");
                return;
            }

            String name = NicknameManager.generateName();
            if (argLength > 1) {
                name = cmd.getArg(1).getText();
                if (Bukkit.getPlayer(name) != null && !sender.hasPermission("serble.nick.bypassfilter")) {
                    sender.sendMessage(Functions.translate("&cYou do not have permission to nick as an online player"));
                    return;
                }
            }

            String rank = "default";
            if (argLength > 2) {
                rank = cmd.getArg(2).getText();
                if (Arrays.stream(getRanks()).noneMatch(rank::equalsIgnoreCase)) {
                    cmd.sendUsage("Invalid rank");
                    return;
                }
            }

            String skin = NicknameManager.randomSkin();
            if (argLength > 3) {
                skin = cmd.getArg(3).getText();
            }

            NicknameManager.nick(target, name, rank, skin);
            sender.sendMessage(Functions.translate("&a" + target.getName() + " has been successfully nicked as " + name));
        } else {
            if (!sender.hasPermission("serble.nick.self")) {
                cmd.sendPermissionError();
                return;
            }
            if (cmd.getSenderType() != CommandSenderType.Player) {
                cmd.sendWrongSenderTypeError();
                return;
            }
            NicknameManager.randomNick((Player) sender);
            sender.sendMessage(Functions.translate("&aYou have been nicked successfully"));
            sender.sendMessage(Functions.translate("&aRejoin for this to fully take effect"));
        }
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return new TabCompletionBuilder(cmd.getArgs())
                .setCase(new TabCompletePlayerResult())
                .setCase(new TabCompleteEnumResult(getRanks()), null, null);
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return ALL_SENDERS;
    }

    private String[] getRanks() {
        List<String> ranks = new ArrayList<>();
        for (Rank rank : Main.config.Ranks) {
            ranks.add(rank.Name);
        }
        return ranks.toArray(new String[0]);
    }
}
