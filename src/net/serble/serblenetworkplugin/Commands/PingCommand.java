package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.UnprocessedCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SlashCommand cmd = new UnprocessedCommand(sender, args)
                .withUsage("/ping [player]")
                .process();

        if (!cmd.isAllowed()) {
            return true;
        }

        Player target = cmd.getArgIgnoreNull(0).getPlayer();
        if (target != null) {
            sender.sendMessage(Functions.translate("&aPing of " + sender.getName() + ": &7" + ((Player) sender).getPing() + "ms"));
            return true;
        }

        if (!(sender instanceof Player)) {
            cmd.sendUsage("Only players can do this");
            return true;
        }

        Player player = (Player) sender;
        sender.sendMessage(Functions.translate("&aPing: &7" + player.getPing() + "ms"));
        return true;
    }

}
