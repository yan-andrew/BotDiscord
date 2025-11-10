package data;

import org.neo4j.driver.*;
import secrets.Config;

import java.time.Duration;

/**
 * Neo4j connection facade.
 * Creates the Driver once, verifies connectivity, and exposes sessions.
 */
public final class ConexionDB implements AutoCloseable {

    private volatile Driver driver;

    /** Initializes the driver once; subsequent calls do nothing. */
    public synchronized void connect() {
        if (driver != null) return;

        final String dbUri = "neo4j+s://" + Config.getIdDB() + ".databases.neo4j.io";
        final String dbUser = Config.getUserDB();
        final String dbPassword = Config.getPasswordDB();

        this.driver = GraphDatabase.driver(dbUri, AuthTokens.basic(dbUser, dbPassword));
        this.driver.verifyConnectivity();
        System.out.println("Connection established.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try { close(); } catch (Exception ignored) {}
        }));
    }

    /** Returns the low-level Driver for advanced use. */
    public Driver getDriver() {
        ensureReady();
        return driver;
    }

    /** Opens a new Session on the default database. */
    public Session newSession() {
        ensureReady();
        return driver.session(SessionConfig.forDatabase("neo4j"));
    }

    private void ensureReady() {
        if (driver == null) {
            throw new IllegalStateException("Driver not initialized. Call connect() first.");
        }
    }

    @Override
    public synchronized void close() {
        if (driver != null) {
            driver.close();
            driver = null;
            System.out.println("Connection closed.");
        }
    }
}