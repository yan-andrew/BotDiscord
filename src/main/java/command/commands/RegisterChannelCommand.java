package command.commands;

import access.creational.ConexionDBSingleton;
import command.core.CommandContext;
import command.core.BotCommand;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;

public class RegisterChannelCommand implements BotCommand {

    @Override public String id() {
        return "channel.register";
    }

    @Override
    public void execute(CommandContext pCommandContext) {
        long guildId, channelId;
        String typeRegister, activity;

        guildId = pCommandContext.guildId();
        channelId = pCommandContext.channelId();
        typeRegister = pCommandContext.invokedName();
        typeRegister = typeRegister.replaceAll("(?i)\\s*channel$", "");

        if (typeRegister.equals("channel")) {
            pCommandContext.reply("Use the commands modslogs channel, VCG channel, " +
                    "tickets channel, advertising channel, " +
                    "poll channel to register the respective channels." , true);
            return;
        }

        activity = registerChannel(channelId, typeRegister, guildId);

        if (activity == null) {
            pCommandContext.reply("Unsupported alias for registration.", true);
            return;
        }

        pCommandContext.reply("Registered channel for activity '" + activity + "' successfully.", true);
    }

    private String registerChannel(long pIdChannel, String pTypeRegister, long pIdSever) {
        String result = null;
        String cypher =
                "MATCH (s:Server {id: $serverId}) " +
                        "MERGE (c:Channel {id: $channelId}) " +
                        "MERGE (s)-[r:USES_CHANNEL {type: $type}]->(c) " +
                        "RETURN s, c, r";

        var conexion = ConexionDBSingleton.getInstance();

        try (Session session = conexion.newSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                transaction.run(cypher, Values.parameters("serverId", pIdSever,
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