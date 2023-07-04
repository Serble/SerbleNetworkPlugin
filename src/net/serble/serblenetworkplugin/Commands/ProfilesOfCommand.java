package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.CacheInvalidationManager;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.PlayerUuidCacheHandler;
import net.serble.serblenetworkplugin.ProfilePermissionsManager;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SerbleCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompleteEnumResult;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletePlayerResult;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletionBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfilesOfCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        Player p = cmd.getPlayerExecutor();

        OfflinePlayer target = cmd.getArg(0) == null ? null : cmd.getArg(0).getOfflinePlayer();
        if (target == null) {
            cmd.sendUsage("Invalid player");
            return;
        }

        if (cmd.getArgs().length == 1) {
            // Display current profile
            UUID profile = PlayerUuidCacheHandler.getInstance().getPlayerUuid(target.getUniqueId());
            String profileName = Main.sqlData.getGameProfileName(profile);
            if (profileName == null) profileName = "default";
            cmd.send("&a" + target.getName() + "'s Active Profile: &7" + profileName);
            cmd.send("&a" + target.getName() + "'s profiles:");
            for (String userPfName : PlayerUuidCacheHandler.getInstance().getProfileList(target.getUniqueId())) {
                cmd.send("&7- " + userPfName);
            }
            return;
        }

        if (cmd.getArgIgnoreNull(1).equalsIgnoreCase("copy")) {
            String profileName = cmd.getArgIgnoreNull(2).getText();
            if (profileName == null) {
                cmd.sendUsage();
                return;
            }

            UUID profile = Main.sqlData.getProfileIdFromName(target.getUniqueId(), profileName);
            if (profile == null && !profileName.equalsIgnoreCase("default")) {
                cmd.sendError("Profile not found!");
                return;
            } else if (profileName.equalsIgnoreCase("default")) {
                profile = target.getUniqueId();
            }

            Main.worldGroupInventoryManager.savePlayerInventory(p);
            ProfilePermissionsManager.removeAllPermissions(p);

            assert profile != null;
            Main.sqlData.setActiveProfile(p.getUniqueId(), profile.toString());
            CacheInvalidationManager.invalidateUuidCachesForPlayer(p.getUniqueId());
            cmd.send("&aProfile set to &7" + profileName + "&a!");
            Main.worldGroupInventoryManager.loadPlayerInventory(p);
            ProfilePermissionsManager.loadPermissions(p);
            return;
        }
        cmd.sendUsage();
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return new TabCompletionBuilder(cmd.getArgs())
                .setCase(new TabCompletePlayerResult())
                .setCase(new TabCompleteEnumResult("copy"), (String) null)
                .setCase(() -> {
                    List<String> results = new ArrayList<>();
                    Player p = cmd.getArgIgnoreNull(0).getPlayer();
                    if (p == null) return results;

                    results.addAll(PlayerUuidCacheHandler.getInstance().getProfileList(p.getUniqueId()));
                    return results;
                }, null, "copy");
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return PLAYER_SENDER;
    }
}
