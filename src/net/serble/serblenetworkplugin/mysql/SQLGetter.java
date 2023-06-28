package net.serble.serblenetworkplugin.mysql;

import net.serble.serblenetworkplugin.AchievementsManager;
import net.serble.serblenetworkplugin.Commands.MySqlLogCommand;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import net.serble.serblenetworkplugin.Schemas.PermissionSettings;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLGetter {

    private void log(String msg) {
        if (!MySqlLogCommand.enabled) return;
        Bukkit.getLogger().info("[MYSQL] " + msg);
    }

    private void checkConnect() {
        try {
            Main.plugin.SQL.connect();
        } catch (SQLException | ClassNotFoundException e) {
            log("&cCould not connect to MySQL database!");
            e.printStackTrace();
        }
    }

    public void createTables() {
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS serble_economy " +
                    "(UUID VARCHAR(36), BALANCE INT(100), PRIMARY KEY (UUID));");
            ps.executeUpdate();

            ps = Main.plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS serble_xp " +
                    "(UUID VARCHAR(36), BALANCE INT(100), PRIMARY KEY (UUID));");
            ps.executeUpdate();

            ps = Main.plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS serble_nicknames " +
                    "(UUID VARCHAR(36), NICK VARCHAR(100), RANKNICK VARCHAR(100), SKIN VARCHAR(100), PRIMARY KEY (UUID));");
            ps.executeUpdate();

            ps = Main.plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS serble_adminmode " +
                    "(UUID VARCHAR(36), ENABLED BOOLEAN, PRIMARY KEY (UUID));");
            ps.executeUpdate();

            // mojang uuid, uuid of active profile
            ps = Main.plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS serble_user_profiles " +
                    "(UUID VARCHAR(36), ACTIVEPROFILE VARCHAR(36) DEFAULT \"0\", PRIMARY KEY (UUID));");
            ps.executeUpdate();

            // uuid of profile, mojang uuid of user, profile name
            ps = Main.plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS serble_profiles " +
                    "(UUID VARCHAR(36), MOJANGUSER VARCHAR(36), PROFILENAME VARCHAR(64), PRIMARY KEY (UUID));");
            ps.executeUpdate();

            ps = Main.plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS serble_achievements " +
                    "(UUID VARCHAR(36), " + AchievementsManager.generateMySqlFieldString() + "PRIMARY KEY (UUID));");
            ps.executeUpdate();

            ps = Main.plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS serble_profile_permissions " +
                    "(UUID VARCHAR(36), NODE VARCHAR(64), VALUE BOOLEAN, PRIMARY KEY (UUID));");
            ps.executeUpdate();

            ps = Main.plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS serble_debug_toggles " +
                    "(UUID VARCHAR(36), VALUE BOOLEAN, PRIMARY KEY (UUID));");
            ps.executeUpdate();

            AchievementsManager.fixMySqlColumns();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerEco(UUID p) {
        log("Creating player eco for " + p.toString());
        try {

            if (existsInEco(p)) return;

            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO serble_economy" +
                    " (UUID) VALUES (?);");
            ps.setString(1, p.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerXp(UUID p) {
        log("Creating player xp for " + p.toString());
        try {

            if (existsInXp(p)) return;

            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO serble_xp" +
                    " (UUID) VALUES (?);");
            ps.setString(1, p.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerNick(UUID p) {
        log("Creating player nick for " + p.toString());
        try {

            if (existsInNicks(p)) return;

            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO serble_nicknames" +
                    " (UUID) VALUES (?);");
            ps.setString(1, p.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerAdminMode(UUID p) {
        log("Creating player admin mode for " + p.toString());
        try {

            if (existsInAdminMode(p)) return;

            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO serble_adminmode" +
                    " (UUID) VALUES (?);");
            ps.setString(1, p.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerDebugToggle(UUID p) {
        log("Creating player debug toggle for " + p.toString());
        try {

            if (existsInDebugToggles(p)) return;

            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO serble_debug_toggles" +
                    " (UUID, VALUE) VALUES (?, ?);");
            ps.setString(1, p.toString());
            ps.setBoolean(2, false);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerAchievements(UUID p) {
        log("Creating player achievements for " + p.toString());
        try {

            if (existsInAchievements(p)) return;

            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO serble_achievements" +
                    " (UUID) VALUES (?);");
            ps.setString(1, p.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerUserProfile(UUID p) {
        log("Creating player user profile for " + p.toString());
        try {

            if (existsInUserProfiles(p)) return;

            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO serble_user_profiles" +
                    " (UUID) VALUES (?);");
            ps.setString(1, p.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void setProfilePermission(UUID p, String node, boolean value) {
        log("Setting profile permission for " + p.toString() + " node: " + node + " value: " + value);
        unsetProfilePermission(p, node);
        try {
            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO serble_profile_permissions" +
                    " (UUID, NODE, VALUE) VALUES (?, ?, ?);");
            ps.setString(1, p.toString());
            ps.setString(2, node);
            ps.setBoolean(3, value);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unsetProfilePermission(UUID p, String node) {
        log("Unsetting profile permission for " + p.toString() + " node: " + node);
        try {
            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("DELETE FROM serble_profile_permissions" +
                    " WHERE UUID=? AND NODE=?;");
            ps.setString(1, p.toString());
            ps.setString(2, node);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PermissionSettings> getProfilePermissions(UUID p) {
        log("Getting profile permissions for " + p.toString());
        List<PermissionSettings> permissions = new ArrayList<>();

        try {
            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT * FROM serble_profile_permissions" +
                    " WHERE UUID=?;");
            ps.setString(1, p.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                permissions.add(new PermissionSettings(rs.getString("NODE"), rs.getBoolean("VALUE")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return permissions;
    }


    public void addMoney(UUID uuid, int amount) {
        log("Adding money to " + uuid.toString() + " amount: " + amount);
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInEco(uuid)) createPlayerEco(uuid);
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_economy SET BALANCE=? WHERE UUID=?");
            ps.setInt(1, getMoney(uuid) + amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMoney(UUID uuid, int amount) {
        log("Setting money for " + uuid.toString() + " amount: " + amount);
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInEco(uuid)) createPlayerEco(uuid);
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_economy SET BALANCE=? WHERE UUID=?");
            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getMoney(UUID uuid) {
        log("Getting money for " + uuid.toString());
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT BALANCE FROM serble_economy WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int bal;
            if (rs.next()) {
                bal = rs.getInt("BALANCE");
                return bal;
            }
            createPlayerEco(Objects.requireNonNull(uuid));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void addXp(UUID uuid, int amount) {
        log("Adding xp to " + uuid.toString() + " amount: " + amount);
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInXp(uuid)) createPlayerXp(uuid);
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_xp SET BALANCE=? WHERE UUID=?");
            ps.setInt(1, getXp(uuid) + amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setXp(UUID uuid, int amount) {
        log("Setting xp for " + uuid.toString() + " amount: " + amount);
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInXp(uuid)) createPlayerXp(uuid);
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_xp SET BALANCE=? WHERE UUID=?");
            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getXp(UUID uuid) {
        log("Getting xp for " + uuid.toString());
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT BALANCE FROM serble_xp WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int bal;
            if (rs.next()) {
                bal = rs.getInt("BALANCE");
                return bal;
            }
            createPlayerXp(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void setAchievement(UUID uuid, Achievement achievement, int progress) {
        log("Setting achievement for " + uuid.toString() + " achievement: " + achievement.toString() + " progress: " + progress);
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInAchievements(uuid)) createPlayerAchievements(uuid);
            ps = Main.plugin.SQL.getConnection().prepareStatement(String.format("UPDATE serble_achievements SET %s=? WHERE UUID=?", achievement.toString()));
            ps.setInt(1, progress);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getAchievement(UUID uuid, Achievement achievement) {
        log("Getting achievement for " + uuid.toString() + " achievement: " + achievement.toString());
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement(String.format("SELECT %s FROM serble_achievements WHERE UUID=?", achievement.toString()));
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int bal;
            if (rs.next()) {
                bal = rs.getInt(achievement.toString());
                return bal;
            }
            createPlayerXp(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public HashMap<Achievement, Integer> getAllPlayerAchievementProgress(UUID uuid) {
        log("Getting all achievements for " + uuid.toString());
        checkConnect();
        PreparedStatement ps;
        HashMap<Achievement, Integer> achievements = new HashMap<>();

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT * FROM serble_achievements WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                for (Achievement achievement : Achievement.values()) {
                    achievements.put(achievement, rs.getInt(achievement.toString()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return achievements;
    }


    public boolean existsInEco(UUID uuid) {
        log("Checking if " + uuid.toString() + " exists in economy");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT * FROM serble_economy WHERE UUID=?;");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsInXp(UUID uuid) {
        log("Checking if " + uuid.toString() + " exists in xp");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT * FROM serble_xp WHERE UUID=?;");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsInNicks(UUID uuid) {
        log("Checking if " + uuid.toString() + " exists in nicks");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT * FROM serble_nicknames WHERE UUID=?;");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsInAdminMode(UUID uuid) {
        log("Checking if " + uuid.toString() + " exists in admin mode");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT * FROM serble_adminmode WHERE UUID=?;");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsInDebugToggles(UUID uuid) {
        log("Checking if " + uuid.toString() + " exists in debug toggles");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT * FROM serble_debug_toggles WHERE UUID=?;");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsInAchievements(UUID uuid) {
        log("Checking if " + uuid.toString() + " exists in achievements");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT * FROM serble_achievements WHERE UUID=?;");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsInUserProfiles(UUID uuid) {
        log("Checking if " + uuid.toString() + " exists in user profiles");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT * FROM serble_user_profiles WHERE UUID=?;");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsInProfiles(String name) {
        log("Checking if " + name + " exists in profiles");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT * FROM serble_profiles WHERE PROFILENAME=?;");
            ps.setString(1, name);
            ResultSet results = ps.executeQuery();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void setNick(UUID uuid, String name) {
        log("Setting " + uuid.toString() + "'s nickname to " + name);
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInNicks(uuid)) createPlayerNick(uuid);
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_nicknames SET NICK=? WHERE UUID=?");
            ps.setString(1, name);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setNickSkin(UUID uuid, String skin) {
        log("Setting " + uuid.toString() + "'s nickname skin to " + skin);
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInNicks(uuid)) createPlayerNick(uuid);
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_nicknames SET SKIN=? WHERE UUID=?");
            ps.setString(1, skin);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setRankNick(UUID uuid, String name) {
        log("Setting " + uuid.toString() + "'s rank nickname to " + name);
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInNicks(uuid)) createPlayerNick(uuid);
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_nicknames SET RANKNICK=? WHERE UUID=?");
            ps.setString(1, name);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getNick(UUID uuid) {
        log("Getting " + uuid.toString() + "'s nickname");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT NICK FROM serble_nicknames WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            String name;
            if (rs.next()) {
                name = rs.getString("NICK");
                return name;
            }
            createPlayerNick(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getNickSkin(UUID uuid) {
        log("Getting " + uuid.toString() + "'s nickname skin");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT SKIN FROM serble_nicknames WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            String name;
            if (rs.next()) {
                name = rs.getString("SKIN");
                return name;
            }
            createPlayerNick(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRankNick(UUID uuid) {
        log("Getting " + uuid.toString() + "'s rank nickname");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT RANKNICK FROM serble_nicknames WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            String name;
            if (rs.next()) {
                name = rs.getString("RANKNICK");
                return name;
            }
            createPlayerNick(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean getAdminMode(UUID uuid) {
        log("Getting " + uuid.toString() + "'s admin mode");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT ENABLED FROM serble_adminmode WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            boolean name;
            if (rs.next()) {
                name = rs.getBoolean("ENABLED");
                return name;
            }
            createPlayerAdminMode(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setAdminMode(UUID uuid, boolean name) {
        log("Setting " + uuid.toString() + "'s admin mode to " + name);
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInAdminMode(uuid)) createPlayerAdminMode(uuid);
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_adminmode SET ENABLED=? WHERE UUID=?");
            ps.setBoolean(1, name);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean getDebugToggle(UUID uuid) {
        log("Getting " + uuid.toString() + "'s debug toggle");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT VALUE FROM serble_debug_toggles WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            boolean name;
            if (rs.next()) {
                name = rs.getBoolean("VALUE");
                return name;
            }
            createPlayerDebugToggle(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setDebugToggle(UUID uuid, boolean name) {
        log("Setting " + uuid.toString() + "'s debug toggle to " + name);
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInDebugToggles(uuid)) createPlayerDebugToggle(uuid);
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_debug_toggles SET VALUE=? WHERE UUID=?");
            ps.setBoolean(1, name);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void setActiveProfile(UUID uuid, String profile) {
        log("Setting " + uuid.toString() + "'s active profile to " + profile);
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInUserProfiles(uuid)) createPlayerUserProfile(uuid);
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_user_profiles SET ACTIVEPROFILE=? WHERE UUID=?");
            ps.setString(1, profile);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UUID getActiveUuid(UUID uuid) {
        log("Getting " + uuid.toString() + "'s active profile");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT ACTIVEPROFILE FROM serble_user_profiles WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            String name;
            if (rs.next()) {
                name = rs.getString("ACTIVEPROFILE");
                if (Objects.equals(name, "0")) return uuid;
                return UUID.fromString(name);
            }
        } catch (SQLException e) {
            Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage("An error occurred while trying to get your active profile. Defaulting to default.");
            throw new RuntimeException(e);
        }
        return uuid;
    }

    public String getGameProfileName(UUID uuid) {
        log("Getting " + uuid.toString() + "'s game profile name");
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT * FROM serble_profiles WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            String name;
            if (rs.next()) {
                name = rs.getString("PROFILENAME");
                return name;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // uuid of profile, mojang uuid of user, profile name UUID VARCHAR(36), MOJANGUSER VARCHAR(36), PROFILENAME VARCHAR(64),
    public void createProfile(UUID owner, UUID profileId, String name) {
        log("Creating profile " + name + " for " + owner.toString());
        try {

            if (existsInProfiles(name)) return;

            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO serble_profiles" +
                    " (UUID, MOJANGUSER, PROFILENAME) VALUES (?, ?, ?);");
            ps.setString(1, profileId.toString());
            ps.setString(2, owner.toString());
            ps.setString(3, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> listUserProfiles(UUID owner) {
        log("Listing profiles for " + owner.toString());
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT PROFILENAME FROM serble_profiles WHERE MOJANGUSER=?");
            ps.setString(1, owner.toString());
            ResultSet rs = ps.executeQuery();
            String name;
            List<String> list = new ArrayList<>();
            while (rs.next()) {
                name = rs.getString("PROFILENAME");
                list.add(name);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public UUID getProfileIdFromName(UUID owner, String name) {
        log("Getting profile id for " + name + " for " + owner.toString());
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT UUID FROM serble_profiles WHERE PROFILENAME=? AND MOJANGUSER=?");
            ps.setString(1, name);
            ps.setString(2, owner.toString());
            ResultSet rs = ps.executeQuery();
            String uuid;
            if (rs.next()) {
                uuid = rs.getString("UUID");
                return UUID.fromString(uuid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UUID getMojangUserFromProfile(UUID profile) {
        log("Getting mojang user for " + profile.toString());
        checkConnect();
        PreparedStatement ps;

        try {
            ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT UUID FROM serble_user_profiles WHERE ACTIVEPROFILE=?");
            ps.setString(1, profile.toString());
            ResultSet rs = ps.executeQuery();
            String uuid;
            if (rs.next()) {
                uuid = rs.getString("UUID");
                return UUID.fromString(uuid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profile;
    }

}
