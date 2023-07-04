package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.NicknameManager;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SerbleCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletePlayerResult;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletionBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class UnnickCmd extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        CommandSender sender = cmd.getExecutor();

        if (!sender.hasPermission("serble.ranknick.self") && !sender.hasPermission("serble.ranknick.others")
                && !sender.hasPermission("serble.nick.self") && !sender.hasPermission("serble.nick.others")) {
            cmd.sendPermissionError();
            return;
        }

        if (cmd.getArgs().length == 1) {
            if (!sender.hasPermission("serble.nick.others")) {
                cmd.sendPermissionError();
                return;
            }

            List<Player> target = cmd.getArgIgnoreNull(0).getPlayerList();
            if (target == null) {
                cmd.sendUsage("Invalid Player");
                return;
            }

            if (sender.hasPermission("serble.nick.others")) {
                for (Player p : target) {
                    NicknameManager.unNick(p);
                }
                sender.sendMessage(Functions.translate("&aUser's disguise has been reset!"));
            }

        } else {
            if (cmd.getSenderType() != CommandSenderType.Player) {
                cmd.sendWrongSenderTypeError();
                return;
            }
            Player p = (Player) sender;

            if (!sender.hasPermission("serble.nick.self")) {
                cmd.sendPermissionError();
                return;
            }

            NicknameManager.unNick(p);
            sender.sendMessage(Functions.translate("&aYour disguise has been reset!"));
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
