package net.serble.serblenetworkplugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.serble.serblenetworkplugin.API.Schemas.WarpEvent;
import net.serble.serblenetworkplugin.Schemas.Party;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PartyManager implements PluginMessageListener {
    private final List<Party> parties = new ArrayList<>();
    private final HashMap<String, String> worldWarpMappings = new HashMap<>();

    public PartyManager() {
        Main.plugin.getConfig().getConfigurationSection("warpworldmappings").getKeys(false).forEach(key -> {
            String world = Main.plugin.getConfig().getString("warpworldmappings." + key);
            this.worldWarpMappings.put(key, world);
        });
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("serble:party")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        boolean fullWarp = false;
        switch (subChannel) {
            default:
                DebugManager.getInstance().debug(player, "&cReceived unknown subchannel for serble:party: " + subChannel);
                break;

            case "fullwarp":
                fullWarp = true;
            case "warp":
                if (!isWarpEnabled()) {
                    return;
                }

                String partyLeaderId = in.readUTF();
                String memberId = in.readUTF();
                Player leader = Main.plugin.getServer().getPlayer(UUID.fromString(partyLeaderId));
                Player member = Main.plugin.getServer().getPlayer(UUID.fromString(memberId));
                if (leader == null) {
                    return;
                }
                if (member == null) {
                    DebugManager.getInstance().debug(leader, "&cMember is not found, failed to warp.");
                    return;
                }

                DebugManager.getInstance().debug(member, "&rWarping to " + leader.getName() + "'s game.");
                WarpEvent event = new WarpEvent(leader, member);
                boolean handled = Main.partyService.sendWarpEventToListeners(event);
                if (handled) break;
                DebugManager.getInstance().debug(member, "Warp was not handled by any plugin, attempting to warp normally.");
                String targetsWorld = worldWarpMappings.getOrDefault(leader.getWorld().getName(), leader.getWorld().getName());
                World world = Bukkit.getWorld(targetsWorld);
                if (world == null) {
                    DebugManager.getInstance().debug(member, "&cMapped world: &7" + targetsWorld + " &cis not found, failed to warp.");
                    return;
                }
                member.teleport(world.getSpawnLocation());
                break;

            case "update":
                // Deserialize party
                Party party = Party.deserialize(in.readUTF());
                DebugManager.getInstance().debug(player, "&rReceived party update for " + party.getLeader() + "'s party.");

                // If it exists, remove it
                parties.removeIf(p -> p.getLeader().equals(party.getLeader()));
                parties.add(party);
                break;

            case "delete":
                String leaderId = in.readUTF();
                parties.removeIf(p -> p.getLeader().toString().equals(leaderId));
                break;

            case "reset":
                parties.clear();
                break;
        }
    }

    public boolean canJoinGame(Player player) {
        return parties.stream().noneMatch(p -> p.isMember(player.getUniqueId()));
    }

    private static boolean isWarpEnabled() {
        return Main.plugin.getConfig().getBoolean("attempttopartywarp");
    }

    public void sendWarpToBungee(Player leader) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("warp");
        out.writeUTF(leader.getUniqueId().toString());
        DebugManager.getInstance().debug(leader, "You have triggered a party warp");
        leader.sendPluginMessage(Main.plugin, "serble:party", out.toByteArray());
    }

}
