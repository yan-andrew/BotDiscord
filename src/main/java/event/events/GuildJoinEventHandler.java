package event.events;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import access.creational.ConexionDBSingleton;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;

/**
 * Handles when the bot joins a new guild.
 */
public final class GuildJoinEventHandler implements EventHandler<GuildJoinEvent> {
    @Override
    public void handleEvent(GuildJoinEvent event) {
        String cypher = "";
        String name = event.getGuild().getName();
        String id = event.getGuild().getId();

        var conexion = ConexionDBSingleton.getInstance();

        try (Session session = conexion.newSession()) {
            cypher = "CREATE (s:Server {id: $id, name: $name}) RETURN s";
            Transaction transaction = session.beginTransaction();
            try {
                transaction.run(cypher, Values.parameters("id", id, "name", name));
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            } finally {
                transaction.close();
            }

        }
    }

    @Override
    public Class<GuildJoinEvent> supports() {
        return GuildJoinEvent.class;
    }
}
