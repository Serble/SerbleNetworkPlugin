package net.serble.serblenetworkplugin.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SlashCommandArgument {
    private final String text;

    public SlashCommandArgument(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public boolean equalsIgnoreCase(String value) {
        if (text == null) return false;
        return text.equalsIgnoreCase(value);
    }

    public Player getPlayer() {
        if (text == null) return null;
        Player namedPlayer = Bukkit.getPlayer(text);
        Player uuidPlayer = Bukkit.getPlayerExact(text);

        if (namedPlayer != null) {
            return namedPlayer;
        }
        return uuidPlayer;
    }

    public OfflinePlayer getOfflinePlayer() {
        if (text == null) return null;
        try {
            return Bukkit.getOfflinePlayer(UUID.fromString(text));
        } catch (IllegalArgumentException ignored) { }
        return Bukkit.getOfflinePlayer(text);
    }

    public Integer getInteger() {
        if (text == null) return null;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double getDouble() {
        if (text == null) return null;
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Float getFloat() {
        if (text == null) return null;
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<Player> getPlayerList() {
        if (text == null) return null;
        List<Player> players = new ArrayList<>();
        for (String s : text.split(",")) {
            Player namedPlayer = Bukkit.getPlayer(s);
            Player uuidPlayer = Bukkit.getPlayerExact(s);
            if (namedPlayer != null) {
                players.add(namedPlayer);
            } else if (uuidPlayer != null) {
                players.add(uuidPlayer);
            }
        }
        if (players.isEmpty()) {
            return null;
        }
        return players;
    }

    public Entity getEntity() {
        if (text == null) return null;
        try {
            return Bukkit.getEntity(UUID.fromString(text));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<Entity> getEntityList() {
        if (text == null) return null;
        List<Entity> entities = new ArrayList<>();
        for (String s : text.split(",")) {
            try {
                entities.add(Bukkit.getEntity(UUID.fromString(s)));
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        return entities;
    }

    public World getWorld() {
        if (text == null) return null;
        return Bukkit.getWorld(text);
    }

    public Boolean getBoolean() {
        if (text == null) return null;
        return text.equalsIgnoreCase("true");
    }

}
