package command.commands;

import access.creational.ConexionDBSingleton;
import access.data.OperationSearch;
import command.core.BotCommand;
import command.core.CommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;

import java.time.Instant;
import java.util.Objects;

public class AutoResponseCommand implements BotCommand {

    @Override public String id() {
        return "auto.response";
    }

    @Override
    public void execute(CommandContext pCommandContext) {
        Guild guild;
        String message, response, actionType, activity = null;
        guild = pCommandContext.guild();
        Event event = pCommandContext.event();

        if (!OperationSearch.verifyAdministrator(guild, pCommandContext.userId())) {
            pCommandContext.reply("It does not have administrative privileges." , true);
            return;
        }

        message = Objects.requireNonNull(pCommandContext.event().getOption("message")).getAsString();

        actionType = pCommandContext.invokedName();

        if (actionType.equals("autoresponse")) {
            pCommandContext.reply("Use add-response when you want to add an automatic " +
                    "response to a message or phrase, or remove-response when you want to " +
                    "remove an automatic response." , true);
            return;
        } else if (actionType.equals("add-response")) {
            response = Objects.requireNonNull(pCommandContext.event().getOption("response")).getAsString();
            addResponse(message, response, guild.getIdLong());
            activity = "Message: " + message + " successfully registered with response " + response;
        } else if (actionType.equals("remove-response")) {
            activity = removeResponse(message, guild.getIdLong());
            activity = "The result of deleting message " + message + " was: " + activity;
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