package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SerbleCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletePlayerResult;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletionBuilder;
import org.bukkit.entity.Player;

public class PingCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        Player target = cmd.getArgIgnoreNull(0).getPlayer();
        if (target != null) {
            cmd.send("&aPing of " + target.getName() + ": &7" + target.getPing() + "ms");
            return;
        }

        if (cmd.getSenderType() != CommandSenderType.Player) {
            cmd.sendError("Only players can do this");
            return;
        }

        Player player = cmd.getPlayerExecutor();
        cmd.send("&aPing: &7" + player.getPing() + "ms");
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
