package access.data;

import access.creational.ConexionDBSingleton;
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
}
