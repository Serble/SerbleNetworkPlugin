package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.DebugManager;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletePlayerResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class SystemDebugCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        List<Player> targets = cmd.getArg(0) == null ? null : cmd.getArg(0).getPlayerList();
        if (targets == null) {
            String msg = cmd.combineArgs(0);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("serble.staff")) {
                    DebugManager.getInstance().debug(p, msg);
                }
            }
            return;
        }

        String msg = cmd.combineArgs(1);
        for (Player p : targets) {
            DebugManager.getInstance().debug(p, msg);
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
