package net.serble.serblenetworkplugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.serble.serblenetworkplugin.API.Schemas.WarpEvent;
import net.serble.serblenetworkplugin.Schemas.Party;
import net.serble.serblenetworkplugin.Schemas.QueuedUserWarp;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.*;

public class PartyManager implements PluginMessageListener, Listener {
    private final List<Party> parties = new ArrayList<>();
    private final HashMap<String, String> worldWarpMappings = new HashMap<>();
    private final List<QueuedUserWarp> queuedUserWarps = new ArrayList<>();

    public PartyManager() {
        Objects.requireNonNull(Main.plugin.getConfig().getConfigurationSection("warpworldmappings")).getKeys(false).forEach(key -> {
            String world = Main.plugin.getConfig().getString("warpworldmappings." + key);
            this.worldWarpMappings.put(key, world);
        });
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        QueuedUserWarp queuedUserWarp = queuedUserWarps.stream().filter(q -> q.player.equals(e.getPlayer().getUniqueId())).findFirst().orElse(null);
        if (queuedUserWarp == null) {
            DebugManager.getInstance().debug(e.getPlayer(), "No queued user warp found for you.");
            return;
        }

        DebugManager.getInstance().debug(e.getPlayer(), "Found queued user warp for you.");
        queuedUserWarps.remove(queuedUserWarp);
        performWarp(e.getPlayer(), queuedUserWarp.leader);
    }

    @EventHandler
    public void playerDisconnect(PlayerQuitEvent e) {
        Party party = getParty(e.getPlayer());
        if (party == null) {
            return;
        }

        // Are any of the members online?
        boolean anyOnline = false;
        for (UUID memberId : party.getMembers()) {
            Player member = Main.plugin.getServer().getPlayer(memberId);
            if (member != null) {
                anyOnline = true;
                break;
            }
        }

        if (!anyOnline) {
            // No members are online, delete the party from memory
            parties.removeIf(p -> p.getLeader().toString().equals(party.getLeader().toString()));
        }
    }

    private void performWarp(Player member, Player leader) {
        DebugManager.getInstance().debug(member, "&rWarping to " + leader.getName() + "'s game.");
        WarpEvent event = new WarpEvent(leader, member);
        boolean handled = Main.partyService.sendWarpEventToListeners(event);
        if (handled) {
            DebugManager.getInstance().debug(member, "Warp was handled by a plugin, not warping normally.");
            return;
        }
        DebugManager.getInstance().debug(member, "Warp was not handled by any plugin, attempting to warp normally.");
        String targetsWorld = worldWarpMappings.getOrDefault(leader.getWorld().getName(), leader.getWorld().getName());
        World world = Bukkit.getWorld(targetsWorld);
        if (world == null) {
            DebugManager.getInstance().debug(member, "&cMapped world: &7" + targetsWorld + " &cis not found, failed to warp.");
            return;
        }
        member.teleport(world.getSpawnLocation());
        DebugManager.getInstance().debug(member, "&rSuccessfully warped to " + leader.getName() + "'s game.");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("serble:party")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        switch (subChannel) {
            default:
                DebugManager.getInstance().debug(player, "&cReceived unknown subchannel for serble:party: " + subChannel);
                break;

            case "fullwarp":
            case "warp":
                if (!isWarpEnabled()) {
                    return;
                }

                String partyLeaderId = in.readUTF();
                String memberId = in.readUTF();
                Player leader = Main.plugin.getServer().getPlayer(UUID.fromString(partyLeaderId));
                Player member = Main.plugin.getServer().getPlayer(UUID.fromString(memberId));
                if (leader == null) {
                    DebugManager.getInstance().serverDebug("&cLeader of party is not found, failed to warp. Leader: " + partyLeaderId);
                    return;
                }
                if (member == null) {
                    DebugManager.getInstance().debug(leader, "Member is not found, queuing user warp.");
                    QueuedUserWarp queuedUserWarp = new QueuedUserWarp();
                    queuedUserWarp.player = UUID.fromString(memberId);
                    queuedUserWarp.leader = leader;
                    queuedUserWarps.add(queuedUserWarp);
                    return;
                }

                performWarp(member, leader);
                break;

            case "update": {
                // Deserialize party
                Party party = Party.deserialize(in.readUTF());

                // If it exists, remove it
                boolean replacedExisting = parties.removeIf(p -> p.getLeader().equals(party.getLeader()));
                parties.add(party);

                Player leaderPlayer = Bukkit.getPlayer(party.getLeader());
                if (leaderPlayer != null) {
                    DebugManager.getInstance().debug(player, "Received party update for your party. Replaced existing: " + replacedExisting);
                }
                break;
            }

            case "delete": {
                String leaderId = in.readUTF();
                boolean removed = parties.removeIf(p -> p.getLeader().toString().equals(leaderId));

                Player leaderPlayer = Bukkit.getPlayer(UUID.fromString(leaderId));
                if (leaderPlayer != null) {
                    DebugManager.getInstance().debug(leaderPlayer, "Received delete request from proxy for your party. Removed: " + removed);
                }
                break;
            }

            case "reset":
                parties.clear();
                DebugManager.getInstance().serverDebug("Proxy sent party cache clear request.");
                break;
        }
    }

    public boolean canJoinGame(Player player) {
        return parties.stream().noneMatch(p -> p.isMember(player.getUniqueId()));
    }

    private static boolean isWarpEnabled() {
        return Main.plugin.getConfig().getBoolean("attempttopartywarp");
    }

    public void sendWarpToBungee(Player leader, boolean delayed) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(delayed ? "delayedwarp" : "warp");
        out.writeUTF(leader.getUniqueId().toString());
        DebugManager.getInstance().debug(leader, "You have triggered a party warp");
        leader.sendPluginMessage(Main.plugin, "serble:party", out.toByteArray());
    }

    public Party getParty(Player player) {
        return parties.stream().filter(p -> p.isMember(player.getUniqueId()) || p.getLeader().equals(player.getUniqueId())).findFirst().orElse(null);
    }

    public List<Party> getParties() {
        return parties;
    }

}
