package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.DebugManager;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import org.bukkit.entity.Player;

public class SerbleDebugCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        Player p = cmd.getPlayerExecutor();
        boolean value = !DebugManager.getInstance().isDebugging(p);
        DebugManager.getInstance().setIsDebugging(p, value);
        cmd.send("&aDebug mode is now " + (value ? "&aenabled" : "&cdisabled"));
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return null;
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return PLAYER_SENDER;
    }
}
