package net.serble.serblenetworkplugin.Commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.serble.serblenetworkplugin.DebugManager;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.UnprocessedCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProxyExecuteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SlashCommand cmd = new UnprocessedCommand(sender, args)
                .withUsage("/proxyexecute <command>")
                .withValidSenders(CommandSenderType.Player)
                .process();

        if (!cmd.isAllowed()) {
            return true;
        }

        Player p = (Player) sender;
        String cmdString = cmd.combineArgs(0);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("execute");
        out.writeUTF(p.getUniqueId().toString());
        out.writeUTF(cmdString);

        p.sendPluginMessage(Main.plugin, "serble:proxyexecute", out.toByteArray());
        DebugManager.getInstance().debug(p, "Executing '" + cmdString + "' on proxy");
        return true;
    }

}
