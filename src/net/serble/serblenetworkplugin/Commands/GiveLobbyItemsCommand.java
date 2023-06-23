package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.MenuItemManager;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.UnprocessedCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveLobbyItemsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SlashCommand cmd = new UnprocessedCommand(sender, args)
                .withPermission("serble.givelobbyitems")
                .withUsage("/givelobbyitems <player>")
                .process();
        if (!cmd.isAllowed()) {
            return true;
        }

        Player target = cmd.getArg(0) == null ? null : cmd.getArg(0).getPlayer();
        if (target == null) {
            cmd.sendUsage("Invalid player");
            return true;
        }

        MenuItemManager.GiveMenuItems(target);

        if (sender instanceof Player) {
            sender.sendMessage(Functions.translate("&aGave " + target.getName() + " lobby items"));
        }
        return true;
    }

}
