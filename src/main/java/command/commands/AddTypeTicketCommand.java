package command.commands;

import access.creational.ConexionDBSingleton;
import access.data.OperationSearch;
import command.core.BotCommand;
import command.core.CommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import java.util.Objects;

public class AddTypeTicketCommand implements BotCommand {

    @Override public String id() {
        return "addtypeticket";
    }

    @Override
    public void execute(CommandContext pCommandContext) {
        Guild guild;
        String cypher, type, description, emoji;
        Role role;
        Value params;
        guild = pCommandContext.guild();

        if (!OperationSearch.verifyAdministrator(guild, pCommandContext.userId())) {
            pCommandContext.reply("It does not have administrative privileges." , true);
            return;
        }

        type = Objects.requireNonNull(pCommandContext.event().getOption("type")).getAsString();
        description = Objects.requireNonNull(pCommandContext.event().getOption("description")).getAsString();
        emoji = Objects.requireNonNull(pCommandContext.event().getOption("emoji")).getAsString();

        try {
            role = pCommandContext.event().getOption("role").getAsRole();
            cypher = "MATCH (s:Server {id: $id}) " +
                            "MERGE (t:TicketType {typeId: $typeId}) " +
                            "SET t.description = $description, t.emoji = $emoji " +
                            "MERGE (s)-[:HAS_TICKET_TYPE]->(t) " +
                            "WITH t " +
                            "MERGE (r:Role {roleId: $roleId}) " +
                            "MERGE (t)-[:MIN_ROLE]->(r)";
            params = Values.parameters(
                    "id", guild.getId(),
                    "typeId", type, "description", description, "emoji", emoji,
                    "roleId", role.getId()
            );
        } catch (Exception e) {
            cypher =
                    "MATCH (s:Server {id: $id}) " +
                            "MERGE (t:TicketType {typeId: $typeId}) " +
                            "SET t.description = $description, t.emoji = $emoji " +
                            "MERGE (s)-[:HAS_TICKET_TYPE]->(t)";
            params = Values.parameters(
                    "id", guild.getId(),
                    "typeId", type, "description", description, "emoji", emoji
            );
        }
        var conexion = ConexionDBSingleton.getInstance();

        try (Session session = conexion.newSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                transaction.run(cypher, params);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            } finally {
                transaction.close();
            }
        }

        pCommandContext.reply("Ticket type " + type + " and minimum role entered correctly." , true);
    }
}