package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletePlayerResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.NicknameManager;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class NickAsCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        OfflinePlayer target = cmd.getArg(0) == null ? null : cmd.getArg(0).getOfflinePlayer();
        if (target == null) {
            cmd.sendUsage("Invalid player");
            return;
        }

        String rank = "default";
        if (target.isOnline()) {
            rank = Functions.getPlayerRank((Player) target, true);
        }

        NicknameManager.nick(cmd.getPlayerExecutor(), target.getName(), rank, target.getName());
        cmd.send("&aYou are now nicked as &7" + target.getName());
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return new TabCompletionBuilder(cmd.getArgs())
                .setCase(new TabCompletePlayerResult());
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return PLAYER_SENDER;
    }
}
