package command.commands;

import access.creational.ConexionDBSingleton;
import command.core.CommandContext;
import command.core.BotCommand;
import net.dv8tion.jda.api.entities.channel.Channel;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;

public class RegisterChannelCommand implements BotCommand {

    @Override public String id() {
        return "channel.register";
    }

    @Override
    public void execute(CommandContext pCommandContext) {
        long guildId;
        String typeRegister, activity, channelId = null;

        Channel channel = pCommandContext.event().getOption("channel").getAsChannel();

        guildId = pCommandContext.guildId();
        channelId = channel.getId();
        typeRegister = pCommandContext.invokedName();
        typeRegister = typeRegister.replaceAll("(?i)\\s*-channel$", "");

        if (typeRegister.equals("channel-register")) {
            pCommandContext.reply("Use the commands modslogs-channel, vcg-channel, " +
                    "tickets-channel, advertising-channel, " +
                    "poll-channel to register the respective channels." , true);
            return;
        }

        activity = registerChannel(channelId, typeRegister, guildId);

        if (activity == null) {
            pCommandContext.reply("Unsupported alias for registration.", true);
            return;
        }

        pCommandContext.reply("Registered channel for activity '" + activity + "' successfully.", true);
    }

    private String registerChannel(String pIdChannel, String pTypeRegister, long pIdSever) {
        String result = null;
        String cypher =
                "MATCH (s:Server {id: $id}) " +
                        "MERGE (c:Channel {channelId: $channelId}) " +
                        "MERGE (s)-[r:USES_CHANNEL {type: $type}]->(c) " +
                        "RETURN s, c, r";

        var conexion = ConexionDBSingleton.getInstance();

        try (Session session = conexion.newSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                transaction.run(cypher, Values.parameters("id", String.valueOf(pIdSever),
                        "channelId", pIdChannel, "type", pTypeRegister));
                transaction.commit();
                result = pTypeRegister;
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            } finally {
                transaction.close();
            }
        }
        return result;
    }
}