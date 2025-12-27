// File: events/handlers/MessageReceivedEventHandler.java
package event.events;

import BotApplication.BotApplication;
import access.creational.ConexionDBSingleton;
import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.util.concurrent.ExecutionException;

/**
 * Handles new incoming messages.
 */
public final class MessageReceivedEventHandler implements EventHandler<MessageReceivedEvent> {

    @Override
    public void handleEvent(MessageReceivedEvent pEvent) throws ExecutionException, InterruptedException {
        User author = pEvent.getAuthor();
        Guild guild = pEvent.getGuild();
        if (author.isBot() || author.isSystem()) {
            return;
        }

        String response = autoResponse(pEvent.getMessage().getContentRaw(), guild.getIdLong());

        if (response != null) {
            MessageBuilder messageBuilder = new MessageBuilder();
            Director director = new Director(messageBuilder);
            director.makeCustom("Response for " + author.getAsMention(), response, pEvent.getChannel(), null);
        }

        BotApplication.addMessage(pEvent.getMessage());
    }

    @Override
    public Class<MessageReceivedEvent> supports() {
        return MessageReceivedEvent.class;
    }

    private String autoResponse(String pMessage, long pId){
        if (pMessage == null) {
            return null;
        }

        String trigger = pMessage.trim().toLowerCase();
        String key = String.valueOf(pId) + "::" + trigger;

        String cypher =
                "MATCH (s:Server {id: $serverId})-[:HAS_AUTORESPONSE]->" +
                        "(ar:AutoResponse {key: $key}) " +
                        "RETURN ar.response AS response";

        var conexion = ConexionDBSingleton.getInstance();

        try (Session session = conexion.newSession()) {
            return session.executeRead(tx -> {
                var result = tx.run(
                        cypher,
                        Values.parameters(
                                "serverId", String.valueOf(pId),
                                "key", key
                        )
                );

                if (!result.hasNext()) {
                    return null;
                }

                var record = result.single();
                if (record.get("response").isNull()) {
                    return null;
                }

                return record.get("response").asString();
            });
        }
    }
}
