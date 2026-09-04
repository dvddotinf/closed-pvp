package closedpvp.storage;

import arc.files.Fi;
import arc.util.Log;
import mindustry.Vars;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private static Connection connection;

    public static void open() {
	if (connection != null) {
	    return;
	}

	try {
	    Class.forName("org.sqlite.JDBC");

	    Fi directory = Vars.dataDirectory.child("closed-pvp");
	    directory.mkdirs();

	    Fi databaseFile = directory.child("players.db");

	    connection = DriverManager.getConnection(
		"jdbc:sqlite:" + databaseFile.file().getAbsolutePath()
	    );

	    configure();
	    createSchema();

	    Log.info(
		"Closed PVP database opened: @",
		databaseFile.file().getAbsolutePath()
	    );
	} catch (Exception exception) {
	    throw new IllegalStateException(
		"Failed to initialize Closed PVP database.",
		exception
	    );
	}
    }

    public static Connection connection() {
	if (connection == null) {
	    throw new IllegalStateException(
		"Closed PVP database is not initialized."
	    );
	}

	return connection;
    }

    public static void close() {
	if (connection == null) {
	    return;
	}

	try {
	    connection.close();
	} catch (SQLException exception) {
	    Log.err("Failed to close Closed PVP database.", exception);
	} finally {
	    connection = null;
	}
    }

    private static void configure() throws SQLException {
	try (Statement statement = connection.createStatement()) {
	    statement.execute("PRAGMA foreign_keys = ON");
	    statement.execute("PRAGMA journal_mode = WAL");
	    statement.execute("PRAGMA busy_timeout = 5000");
	}
    }

    private static void createSchema() throws SQLException {
	try (Statement statement = connection.createStatement()) {
	    statement.executeUpdate("""
		CREATE TABLE IF NOT EXISTS players (
		    id          INTEGER PRIMARY KEY AUTOINCREMENT,
		    uuid        TEXT NOT NULL UNIQUE,
		    usid        TEXT,
		    last_name   TEXT NOT NULL,
		    created_at  INTEGER NOT NULL,
		    last_seen_at INTEGER NOT NULL
		)
		""");
	}
    }

    private Database() {
    }
}
