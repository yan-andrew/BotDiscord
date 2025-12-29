package access.data;

import access.creational.ConexionDBSingleton;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import systemtickets.viewmodel.TicketTypeVM;

import java.util.ArrayList;
import java.util.Optional;

import static org.neo4j.driver.Values.parameters;

public class OperationSearch {

    public static Optional<String> findChannelIdForServer(String pIdServer, String pType) {
        String cypher = "MATCH (s:Server {id: $serverId})-[:USES_CHANNEL {type: $type}]->(c:Channel) " +
                "RETURN c.channelId AS channelId";

        var conexion = ConexionDBSingleton.getInstance();

        try (Session session = conexion.newSession()) {
            return session.executeRead(tx -> {
                Result result = tx.run(cypher, parameters(
                        "serverId", pIdServer,
                        "type", pType
                ));

                if (result.hasNext()) {
                    Record record = result.next();
                    String channelId = (record).get("channelId").asString();
                    return Optional.of(channelId);
                }
                return Optional.empty();
            });
        }
    }

    public static boolean verifyAdministrator(Guild pGuild, long pIdUser) {
        if (pGuild == null) {
            return false;
        }

        Member member = pGuild.getMemberById(pIdUser);
        if (member == null) {
            try {
                member = pGuild.retrieveMemberById(pIdUser).complete();
            } catch (ErrorResponseException e) {
                return false;
            }
        }

        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    public static Optional<String> getRolesByTicketType(long pGuildId, String pType) {
        String cypher =
                "MATCH (s:Server {id: $serverId})-[:HAS_TICKET_TYPE]->(t:TicketType {typeId: $typeId}) " +
                        "OPTIONAL MATCH (t)-[:MIN_ROLE]->(r:Role) " +
                        "RETURN r.roleId AS roleId";

        var conexion = ConexionDBSingleton.getInstance();

        try (Session session = conexion.newSession()) {
            Transaction tx = session.beginTransaction();
            try {
                var result = tx.run(
                        cypher,
                        Values.parameters(
                                "serverId", String.valueOf(pGuildId),
                                "typeId", pType
                        )
                );

                if (!result.hasNext()) return Optional.empty();

                Record record = result.next();
                if (record.get("roleId").isNull()) return Optional.empty();

                return Optional.of(record.get("roleId").asString());
            } catch (Exception e) {
                tx.rollback();
                throw e;
            } finally {
                tx.close();
            }
        }
    }

    public static ArrayList<TicketTypeVM> getTicketTypesForPanel(long serverId) {

        ArrayList<TicketTypeVM> result = new ArrayList<>();

        String cypher =
                "MATCH (s:Server {id: $id})-[:HAS_TICKET_TYPE]->(t:TicketType) " +
                        "RETURN t.typeId AS typeId, t.description AS description, t.emoji AS emoji";

        var conexion = ConexionDBSingleton.getInstance();

        try (Session session = conexion.newSession()) {
            session.run(
                    cypher,
                    Values.parameters("id", String.valueOf(serverId))
            ).forEachRemaining(record -> {
                String id = record.get("id").asString();
                String typeId = record.get("typeId").asString();
                String description = record.get("description").asString();
                String emoji = record.get("emoji").asString();
                result.add(new TicketTypeVM(typeId, typeId, description, emoji));
            });
        }

        return result;
    }

}
