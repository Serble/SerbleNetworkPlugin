package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.DebugManager;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.UnprocessedCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SystemDebugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SlashCommand cmd = new UnprocessedCommand(sender, args).withPermission("serble.sysdebug").process();
        if (!cmd.isAllowed()) return true;

        List<Player> targets = cmd.getArg(0) == null ? null : cmd.getArg(0).getPlayerList();
        if (targets == null) {
            String msg = cmd.combineArgs(0);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("serble.staff")) {
                    DebugManager.getInstance().debug(p, msg);
                }
            }
            return true;
        }

        String msg = cmd.combineArgs(1);
        for (Player p : targets) {
            DebugManager.getInstance().debug(p, msg);
        }
        return true;
    }

}
