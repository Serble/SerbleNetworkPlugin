package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.NicknameManager;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.UnprocessedCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickAsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SlashCommand cmd = new UnprocessedCommand(sender, args)
                .withPermission("serble.becomeplayer")
                .withUsage("/nickas <player>")
                .withValidSenders(CommandSenderType.Player)
                .process();
        if (!cmd.isAllowed()) {
            return true;
        }

        OfflinePlayer target = cmd.getArg(0) == null ? null : cmd.getArg(0).getOfflinePlayer();
        if (target == null) {
            cmd.sendUsage("Invalid player");
            return true;
        }

        String rank = "default";
        if (target.isOnline()) {
            rank = Functions.getPlayerRank((Player) target, true);
        }

        NicknameManager.nick((Player) sender, target.getName(), rank, target.getName());

        sender.sendMessage(Functions.translate("&aYou are now nicked as &7" + target.getName()));
        return true;
    }

}
