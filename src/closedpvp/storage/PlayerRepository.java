package closedpvp.storage;

import mindustry.gen.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class PlayerRepository {
    public static PlayerRecord upsert(Player player) {
        String uuid = player.uuid();
        String usid = player.usid();
        String name = player.plainName();
        long now = System.currentTimeMillis();

        try (
            PreparedStatement statement = Database.connection().prepareStatement("""
                INSERT INTO players (
                    uuid,
                    usid,
                    last_name,
                    created_at,
                    last_seen_at
                )
                VALUES (?, ?, ?, ?, ?)

                ON CONFLICT(uuid) DO UPDATE SET
                    usid = excluded.usid,
                    last_name = excluded.last_name,
                    last_seen_at = excluded.last_seen_at
                """)
        ) {
            statement.setString(1, uuid);
            statement.setString(2, usid);
            statement.setString(3, name);
            statement.setLong(4, now);
            statement.setLong(5, now);

            statement.executeUpdate();

            return findByUuid(uuid);
        } catch (SQLException exception) {
            throw new IllegalStateException(
                "Failed to save player " + uuid,
                exception
            );
        }
    }

    public static PlayerRecord findByUuid(String uuid) {
        try (
            PreparedStatement statement = Database.connection().prepareStatement("""
                SELECT
                    id,
                    uuid,
                    usid,
                    last_name,
                    created_at,
                    last_seen_at
                FROM players
                WHERE uuid = ?
                """)
        ) {
            statement.setString(1, uuid);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                return new PlayerRecord(
                    result.getLong("id"),
                    result.getString("uuid"),
                    result.getString("usid"),
                    result.getString("last_name"),
                    result.getLong("created_at"),
                    result.getLong("last_seen_at")
                );
            }
        } catch (SQLException exception) {
            throw new IllegalStateException(
                "Failed to load player " + uuid,
                exception
            );
        }
    }

    public static void touch(Player player) {
        try (
            PreparedStatement statement = Database.connection().prepareStatement("""
                UPDATE players
                SET
                    usid = ?,
                    last_name = ?,
                    last_seen_at = ?
                WHERE uuid = ?
                """)
        ) {
            statement.setString(1, player.usid());
            statement.setString(2, player.plainName());
            statement.setLong(3, System.currentTimeMillis());
            statement.setString(4, player.uuid());

            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException(
                "Failed to update player " + player.uuid(),
                exception
            );
        }
    }

    private PlayerRepository() {
    }
}
