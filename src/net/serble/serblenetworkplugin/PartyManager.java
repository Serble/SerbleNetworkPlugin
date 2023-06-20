package net.serble.serblenetworkplugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class PartyManager implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("serble:party")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        if (!subChannel.equals("warp") && !subChannel.equals("fullwarp")) {
            return;
        }

        DebugManager.getInstance().debug(player, "&rReceived request to warp you to a game mode.");

        String partyLeaderId = in.readUTF();
        Player leader = Main.plugin.getServer().getPlayer(UUID.fromString(partyLeaderId));
        if (leader == null) {
            DebugManager.getInstance().debug(player, "&cParty leader is not found, failed to warp.");
            return;
        }

        DebugManager.getInstance().debug(player, "&rWarping to " + leader.getName() + "'s game.");
    }

}
