package messenger.build;

import access.creational.ConexionDBSingleton;
import messenger.messaging.Message;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.awt.*;
import java.util.Optional;

import static org.neo4j.driver.Values.parameters;

public class Director {
    private Builder builder;

    public Director(Builder pBuilder) {
        builder = pBuilder;
    }

    private Optional<String> findChannelIdForServer(String pIdServer, String pType) {
        String cypher = "MATCH (s:Server {id: $serverId})-[:USES_CHANNEL {type: $type}]->(c:Channel) " +
                        "RETURN c.id AS channelId";

        var conexion = ConexionDBSingleton.getInstance();

        try (Session session = conexion.newSession()) {
            return session.executeRead(tx -> {
                Result result = tx.run(cypher, parameters(
                        "serverId", pIdServer,
                        "type", pType
                ));

                if (result.hasNext()) {
                    Record record = (Record) result.next();
                    String channelId = ((org.neo4j.driver.Record) record).get("channelId").asString();
                    return Optional.of(channelId);
                }
                return Optional.empty();
            });
        }
    }

    public void makeBan(User pUser, String pReason, Guild pGuild) {
        String title = "Ban";
        String content = "User: " + pUser.getAsMention() + "\n"
                + "Reason: " + pReason;
        String idServer = pGuild.getId();


        Optional<String> logChannel = findChannelIdForServer(idServer, "modslogs");
        String channelId = logChannel.get();
        MessageChannel channel = pGuild.getChannelById(GuildMessageChannel.class, channelId);

        builder.setType("Embed");
        builder.reset();
        builder.buildTitle(title);
        builder.buildContent(content);
        builder.buildChannel(channel);
        builder.buildColor(Color.RED);

        Message result = builder.getResult();
        result.sendMessage();
    }
}
