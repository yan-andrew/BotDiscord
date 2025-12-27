package access.data;

import access.creational.ConexionDBSingleton;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

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
}
