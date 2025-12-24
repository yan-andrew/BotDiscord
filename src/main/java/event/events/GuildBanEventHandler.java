package event.events;

import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;

public class GuildBanEventHandler implements EventHandler<GuildBanEvent> {
    @Override
    public void handleEvent(GuildBanEvent event) {
        String cypher;
        User user;
        Guild guild;

        guild = event.getGuild();
        user = event.getUser();
        guild.retrieveAuditLogs()
                .type(ActionType.BAN)
                .limit(1)
                .queue(logs -> {
                    String reason = "No reason provided";
                    User moderator = null;

                    for (AuditLogEntry entry : logs) {
                        if (entry.getTargetIdLong() == user.getIdLong()) {
                            reason = entry.getReason() != null
                                    ? entry.getReason()
                                    : reason;
                            moderator = entry.getUser(); // QUIÉN aplicó la sanción
                            break;
                        }
                    }

                    MessageBuilder messageBuilder = new MessageBuilder();
                    Director director = new Director(messageBuilder);
                    director.makeBan(user, reason, moderator, guild);
                });
    }

    @Override
    public Class<GuildBanEvent> supports() {
        return GuildBanEvent.class;
    }
}
