package net.serble.serblenetworkplugin;

import net.serble.serblenetworkplugin.Schemas.PermissionSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ProfilePermissionsManager implements Listener {

    private static final HashMap<UUID, PermissionAttachment> permissionAttachments = new HashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        permissionAttachments.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        loadPermissions(e.getPlayer());
    }

    public static void loadPermissions(Player player) {
        UUID uuid = GameProfileUtils.getPlayerUuid(player);
        List<PermissionSettings> permissions = Main.sqlData.getProfilePermissions(uuid);

        // Add all permissions to the player
        PermissionAttachment attachment = player.addAttachment(Main.plugin);
        for (PermissionSettings permission : permissions) {
            attachment.setPermission(permission.node, permission.value);
        }
        permissionAttachments.put(player.getUniqueId(), attachment);
    }

    public static void removeAllPermissions(Player player) {
        PermissionAttachment attachment = permissionAttachments.get(player.getUniqueId());
        if (attachment == null) return;
        attachment.remove();
        permissionAttachments.remove(player.getUniqueId());
    }

    public static void addPermission(UUID profile, PermissionSettings permission) {
        PermissionAttachment attachment = permissionAttachments.get(GameProfileUtils.getPlayerFromProfile(profile));
        if (attachment == null) return;
        attachment.unsetPermission(permission.node);
        attachment.setPermission(permission.node, permission.value);

        Main.sqlData.setProfilePermission(profile, permission.node, permission.value);
    }

    public static void removePermission(UUID profile, PermissionSettings permission) {
        PermissionAttachment attachment = permissionAttachments.get(GameProfileUtils.getPlayerFromProfile(profile));
        if (attachment == null) return;
        attachment.unsetPermission(permission.node);

        Main.sqlData.unsetProfilePermission(profile, permission.node);
    }


}
