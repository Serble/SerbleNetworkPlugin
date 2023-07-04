package net.serble.serblenetworkplugin.Commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.serble.serblenetworkplugin.DebugManager;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SerbleCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletionBuilder;
import org.bukkit.entity.Player;

public class ProxyExecuteCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        Player p = cmd.getPlayerExecutor();
        String cmdString = cmd.combineArgs(0);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("execute");
        out.writeUTF(p.getUniqueId().toString());
        out.writeUTF(cmdString);

        p.sendPluginMessage(Main.plugin, "serble:proxyexecute", out.toByteArray());
        DebugManager.getInstance().debug(p, "Executing '" + cmdString + "' on proxy");
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
