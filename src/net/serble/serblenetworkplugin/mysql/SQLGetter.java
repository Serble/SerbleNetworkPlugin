package net.serble.serblenetworkplugin.mysql;

import net.serble.serblenetworkplugin.AchievementsManager;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class SQLGetter {

    private void checkConnect() {
        try {
            Main.plugin.SQL.connect();
        } catch (SQLException | ClassNotFoundException e) {
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
                    "(UUID VARCHAR(36), NICK VARCHAR(100), RANKNICK VARCHAR(100), PRIMARY KEY (UUID));");
            ps.executeUpdate();

            ps = Main.plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS serble_adminmode " +
                    "(UUID VARCHAR(36), ENABLED BOOLEAN, PRIMARY KEY (UUID));");
            ps.executeUpdate();

            ps = Main.plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS serble_achievements " +
                    "(UUID VARCHAR(36), " + AchievementsManager.generateMySqlFieldString() + "PRIMARY KEY (UUID));");
            ps.executeUpdate();

            AchievementsManager.fixMySqlColumns();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerEco(Player p) {
        try {
            UUID uuid = p.getUniqueId();

            if (existsInEco(uuid)) return;

            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO serble_economy" +
                    " (UUID) VALUES (?);");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerXp(Player p) {
        try {
            UUID uuid = p.getUniqueId();

            if (existsInXp(uuid)) return;

            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO serble_xp" +
                    " (UUID) VALUES (?);");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerNick(Player p) {
        try {
            UUID uuid = p.getUniqueId();

            if (existsInNicks(uuid)) return;

            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO serble_nicknames" +
                    " (UUID) VALUES (?);");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerAdminMode(Player p) {
        try {
            UUID uuid = p.getUniqueId();

            if (existsInAdminMode(uuid)) return;

            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO serble_adminmode" +
                    " (UUID) VALUES (?);");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerAchievements(Player p) {
        try {
            UUID uuid = p.getUniqueId();

            if (existsInAchievements(uuid)) return;

            PreparedStatement ps = Main.plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO serble_achievements" +
                    " (UUID) VALUES (?);");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void addMoney(UUID uuid, int amount) {
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInEco(uuid)) createPlayerEco(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_economy SET BALANCE=? WHERE UUID=?");
            ps.setInt(1, getMoney(uuid) + amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMoney(UUID uuid, int amount) {
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInEco(uuid)) createPlayerEco(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_economy SET BALANCE=? WHERE UUID=?");
            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getMoney(UUID uuid) {
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
            createPlayerEco(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void addXp(UUID uuid, int amount) {
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInXp(uuid)) createPlayerXp(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_xp SET BALANCE=? WHERE UUID=?");
            ps.setInt(1, getXp(uuid) + amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setXp(UUID uuid, int amount) {
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInXp(uuid)) createPlayerXp(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_xp SET BALANCE=? WHERE UUID=?");
            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getXp(UUID uuid) {
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
            createPlayerXp(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void addAchievementProgress(UUID uuid, Achievement achievement, int amount) {
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInAchievements(uuid)) createPlayerAchievements(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
            ps = Main.plugin.SQL.getConnection().prepareStatement(String.format("UPDATE serble_achievements SET %s=? WHERE UUID=?", achievement.toString()));
            ps.setInt(1, getAchievement(uuid, achievement) + amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setAchievement(UUID uuid, Achievement achievement, int progress) {
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInAchievements(uuid)) createPlayerAchievements(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
            ps = Main.plugin.SQL.getConnection().prepareStatement(String.format("UPDATE serble_achievements SET %s=? WHERE UUID=?", achievement.toString()));
            ps.setInt(1, progress);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getAchievement(UUID uuid, Achievement achievement) {
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
            createPlayerXp(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public boolean existsInEco(UUID uuid) {
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

    public boolean existsInAchievements(UUID uuid) {
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


    public void setNick(UUID uuid, String name) {
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInNicks(uuid)) createPlayerNick(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_nicknames SET NICK=? WHERE UUID=?");
            ps.setString(1, name);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setRankNick(UUID uuid, String name) {
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInNicks(uuid)) createPlayerNick(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_nicknames SET RANKNICK=? WHERE UUID=?");
            ps.setString(1, name);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getNick(UUID uuid) {
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
            createPlayerNick(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRankNick(UUID uuid) {
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
            createPlayerNick(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean getAdminMode(UUID uuid) {
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
            createPlayerAdminMode(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setAdminMode(UUID uuid, boolean name) {
        checkConnect();
        PreparedStatement ps;

        try {
            if (!existsInAdminMode(uuid)) createPlayerAdminMode(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
            ps = Main.plugin.SQL.getConnection().prepareStatement("UPDATE serble_adminmode SET ENABLED=? WHERE UUID=?");
            ps.setBoolean(1, name);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
