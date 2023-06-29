package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.*;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.UnprocessedCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfilesOfCommand implements CommandExecutor, TabCompleter {

    // TODO: This function
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SlashCommand cmd = new UnprocessedCommand(sender, args)
                .withPermission("serble.profiles.admin")
                .withUsage("/profilesof <player> [copy] [profile]")
                .withValidSenders(CommandSenderType.Player)
                .process();

        if (!cmd.isAllowed()) {
            return true;
        }
        Player p = (Player) sender;

        OfflinePlayer target = cmd.getArg(0) == null ? null : cmd.getArg(0).getOfflinePlayer();
        if (target == null) {
            cmd.sendUsage("Invalid player");
            return true;
        }

        if (args.length == 1) {
            // Display current profile
            UUID profile = PlayerUuidCacheHandler.getInstance().getPlayerUuid(target.getUniqueId());
            String profileName = Main.sqlData.getGameProfileName(profile);
            if (profileName == null) profileName = "default";
            p.sendMessage(Functions.translate("&a" + target.getName() + "'s Active Profile: &7" + profileName));
            p.sendMessage(Functions.translate("&a" + target.getName() + "'s profiles:"));
            for (String userPfName : PlayerUuidCacheHandler.getInstance().getProfileList(target.getUniqueId())) {
                p.sendMessage(Functions.translate("&7- " + userPfName));
            }
            return true;
        }

        if (cmd.getArgIgnoreNull(1).equalsIgnoreCase("copy")) {
            if (args.length != 3) {
                cmd.sendUsage();
                return true;
            }

            String profileName = args[2];

            UUID profile = Main.sqlData.getProfileIdFromName(target.getUniqueId(), profileName);
            if (profile == null && !profileName.equalsIgnoreCase("default")) {
                p.sendMessage(Functions.translate("&cProfile not found!"));
                return true;
            } else if (profileName.equalsIgnoreCase("default")) {
                profile = target.getUniqueId();
            }

            Main.worldGroupInventoryManager.savePlayerInventory(p);
            ProfilePermissionsManager.removeAllPermissions(p);

            assert profile != null;
            Main.sqlData.setActiveProfile(p.getUniqueId(), profile.toString());
            CacheInvalidationManager.invalidateUuidCachesForPlayer(p.getUniqueId());
            p.sendMessage(Functions.translate("&aProfile set to &7" + profileName + "&a!"));
            Main.worldGroupInventoryManager.loadPlayerInventory(p);
            ProfilePermissionsManager.loadPermissions(p);
            return true;
        }

        cmd.sendUsage();
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<>();

        if (!(sender instanceof Player)) return results;
        if (!sender.hasPermission("serble.profiles")) return results;
        Player p = (Player) sender;

        if (args.length == 1) {
            results.add("add");
            results.add("set");
            return results;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            for (String profile : PlayerUuidCacheHandler.getInstance().getProfileList(p.getUniqueId())) {
                if (!profile.toLowerCase().startsWith(args[1].toLowerCase())) continue;
                results.add(profile);
            }
        }

        return results;
    }

}
