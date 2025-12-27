package event.events;

import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;

public final class GuildUnBanEventHandler implements EventHandler<GuildUnbanEvent> {
    @Override
    public void handleEvent(GuildUnbanEvent event) {
        User user;
        Guild guild;

        guild = event.getGuild();
        user = event.getUser();
        guild.retrieveAuditLogs()
                .type(ActionType.UNBAN)
                .limit(1)
                .queue(logs -> {
                    User moderator = null;

                    for (AuditLogEntry entry : logs) {
                        if (entry.getTargetIdLong() == user.getIdLong()) {
                            moderator = entry.getUser();
                            break;
                        }
                    }

                    MessageBuilder messageBuilder = new MessageBuilder();
                    Director director = new Director(messageBuilder);
                    director.makeUnBan(user, moderator, guild);
                });
    }

    @Override
    public Class<GuildUnbanEvent> supports() {
        return GuildUnbanEvent.class;
    }
}
