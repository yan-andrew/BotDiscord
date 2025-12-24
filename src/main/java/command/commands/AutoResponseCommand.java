package command.commands;

import access.creational.ConexionDBSingleton;
import command.core.BotCommand;
import command.core.CommandContext;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;

import java.time.Instant;

public class AutoResponseCommand implements BotCommand {

    @Override public String id() {
        return "auto.response";
    }

    @Override
    public void execute(CommandContext pCommandContext) {
        long guildId;
        String message, response, actionType, activity = null;

        message = String.valueOf(pCommandContext.event().getOption("message"));
        response = String.valueOf(pCommandContext.event().getOption("response"));

        guildId = pCommandContext.guildId();
        actionType = pCommandContext.invokedName();

        if (actionType.equals("autoresponse")) {
            pCommandContext.reply("Use add-response when you want to add an automatic " +
                    "response to a message or phrase, or remove-response when you want to " +
                    "remove an automatic response." , true);
            return;
        } else if (actionType.equals("add-response")) {
            activity = addResponse(message, response, guildId);
            activity = "Message: " + activity + " successfully registered with response " + response;
        } else if (actionType.equals("remove-response")) {
            activity = removeResponse(message, guildId);
            activity = "The result of deleting message " + message + " was:" + activity;
        }

        if (activity == null) {
            pCommandContext.reply("Unsupported alias for registration.", true);
            return;
        }

        pCommandContext.reply(activity, true);
    }

    private String addResponse(String pMessage, String pResponse, long pIdSever) {
        String result = null;

        String trigger = (pMessage == null) ? "" : pMessage.trim().toLowerCase();
        String key = String.valueOf(pIdSever) + "::" + trigger;
        long updatedAt = Instant.now().getEpochSecond();

        String cypher =
                "MERGE (s:Server {id: $serverId}) " +
                        "MERGE (ar:AutoResponse {key: $key}) " +
                        "SET ar.trigger = $trigger, " +
                        "    ar.response = $response, " +
                        "    ar.updatedAt = $updatedAt " +
                        "MERGE (s)-[:HAS_AUTORESPONSE]->(ar) " +
                        "RETURN ar.key AS key";

        var conexion = ConexionDBSingleton.getInstance();

        try (Session session = conexion.newSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                var queryResult = transaction.run(
                        cypher,
                        Values.parameters(
                                "serverId", String.valueOf(pIdSever),
                                "key", key,
                                "trigger", trigger,
                                "response", pResponse,
                                "updatedAt", updatedAt
                        )
                );

                if (queryResult.hasNext()) {
                    result = queryResult.single().get("key").asString();
                }

                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            } finally {
                transaction.close();
            }
        }

        return result;
    }

    private String removeResponse(String pMessage, long pIdSever) {

        String trigger = (pMessage == null) ? "" : pMessage.trim().toLowerCase();
        String key = String.valueOf(pIdSever) + "::" + trigger;

        String cypher =
                "MATCH (s:Server {id: $serverId})-[:HAS_AUTORESPONSE]->" +
                        "(ar:AutoResponse {key: $key}) " +
                        "DETACH DELETE ar " +
                        "RETURN count(ar) AS deleted";

        var conexion = ConexionDBSingleton.getInstance();

        try (Session session = conexion.newSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                var result = transaction.run(
                        cypher,
                        Values.parameters(
                                "serverId", String.valueOf(pIdSever),
                                "key", key
                        )
                );

                boolean deleted = false;
                if (result.hasNext()) {
                    deleted = result.single().get("deleted").asInt() > 0;
                }

                transaction.commit();
                return "Deleted";

            } catch (Exception e) {
                transaction.rollback();
                throw e;
            } finally {
                transaction.close();
            }
        }
    }
}