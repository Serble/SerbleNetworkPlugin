package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.Cache.CacheInvalidationManager;
import net.serble.serblenetworkplugin.Cache.PlayerUuidCacheHandler;
import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommandArgument;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompleteEnumResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompleteProfilesResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.DebugManager;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.ProfilePermissionsManager;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ProfileCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        Player p = cmd.getPlayerExecutor();

        SlashCommandArgument firstArg = cmd.getArgIgnoreNull(0);
        if (cmd.getArgs().length == 0) {
            // Display current profile
            UUID profile = PlayerUuidCacheHandler.getInstance().getPlayerUuid(p.getUniqueId());
            DebugManager.getInstance().debug(p, "Profile UUID: " + profile);
            String profileName = PlayerUuidCacheHandler.getInstance().getProfileName(profile);

            cmd.send("&aActive Profile: &7" + profileName);
            cmd.send("&aYour profiles:");
            for (String userPfName : PlayerUuidCacheHandler.getInstance().getProfileList(p.getUniqueId())) {
                cmd.send("&7- " + userPfName);
            }
            return;
        }

        if (firstArg.equalsIgnoreCase("add")) {
            if (PlayerUuidCacheHandler.getInstance().getProfileList(p.getUniqueId()).size() >= 50) {
                cmd.sendError("You cannot have more than 50 profiles!");
                return;
            }

            String profileName = cmd.getArgIgnoreNull(1).getText();
            if (profileName == null) {
                cmd.sendUsage();
                return;
            }

            Main.sqlData.createProfile(p.getUniqueId(), UUID.randomUUID(), profileName);
            PlayerUuidCacheHandler.getInstance().invalidateProfileList(p.getUniqueId());
            cmd.send("&aProfile &7" + profileName + "&a added!");
            return;
        }

        if (firstArg.equalsIgnoreCase("set")) {
            String profileName = cmd.getArgIgnoreNull(1).getText();
            if (profileName == null) {
                cmd.sendUsage();
                return;
            }

            UUID profile = Main.sqlData.getProfileIdFromName(p.getUniqueId(), profileName);
            if (profile == null && !profileName.equalsIgnoreCase("default")) {
                cmd.sendError("Profile not found!");
                return;
            } else if (profileName.equalsIgnoreCase("default")) {
                profile = p.getUniqueId();
            }

            Main.worldGroupInventoryManager.savePlayerInventory(p);
            ProfilePermissionsManager.removeAllPermissions(p);

            assert profile != null;
            Main.sqlData.setActiveProfile(p.getUniqueId(), profile == p.getUniqueId() ? "0" : profile.toString());
            CacheInvalidationManager.invalidateUuidCachesForPlayer(p.getUniqueId());
            p.sendMessage(Functions.translate("&aProfile set to &7" + profileName + "&a!"));
            Main.worldGroupInventoryManager.loadPlayerInventory(p);
            ProfilePermissionsManager.loadPermissions(p);
        }
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return new TabCompletionBuilder(cmd.getArgs())
                .setCase(new TabCompleteEnumResult("add", "set"))
                .setCase(new TabCompleteProfilesResult(cmd.getPlayerExecutor()), "set");
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return PLAYER_SENDER;
    }
}
