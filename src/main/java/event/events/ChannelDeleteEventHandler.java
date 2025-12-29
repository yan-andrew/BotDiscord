package event.events;

import access.creational.ConexionDBSingleton;
import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;

public final class ChannelDeleteEventHandler implements EventHandler<ChannelDeleteEvent> {
    @Override
    public void handleEvent(ChannelDeleteEvent event) {
        Guild guild;
        guild = event.getGuild();
        User user = null;
        Channel channel = event.getChannel();
        String channelName, channelId;

        try {
            java.util.List<AuditLogEntry> logs = guild.retrieveAuditLogs()
                    .type(ActionType.CHANNEL_DELETE)
                    .limit(1)
                    .complete();

            for (AuditLogEntry entry : logs) {
                if (entry.getTargetIdLong() == channel.getIdLong()) {
                    user = entry.getUser();
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channelName = channel.getName();
            channelId = channel.getId();

            String cypher =
                    "MATCH (s:Server {id: $serverId})-[:USES_CHANNEL]->" +
                            "(ar:Channel {channelId: $channelId}) " +
                            "DETACH DELETE ar " +
                            "RETURN count(ar) AS deleted";

            var conexion = ConexionDBSingleton.getInstance();

            try (Session session = conexion.newSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    transaction.run(cypher, Values.parameters(
                                    "serverId", guild.getId(),
                                    "channelId", channelId)
                    );

                    transaction.commit();

                } catch (Exception e) {
                    transaction.rollback();
                    throw e;
                } finally {
                    transaction.close();
                }
            }

            MessageBuilder messageBuilder = new MessageBuilder();
            Director director = new Director(messageBuilder);
            director.makeChannelDelete(channelName, channelId, guild, user);
        }
    }

    @Override
    public Class<ChannelDeleteEvent> supports() {
        return ChannelDeleteEvent.class;
    }
}
