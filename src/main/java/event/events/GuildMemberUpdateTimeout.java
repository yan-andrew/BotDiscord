package event.events;

import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateTimeOutEvent;

import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicReference;

public final class GuildMemberUpdateTimeout implements EventHandler<GuildMemberUpdateTimeOutEvent> {
    @Override
    public void handleEvent(GuildMemberUpdateTimeOutEvent event) {
        Guild guild = event.getGuild();
        String title, content;
        final AtomicReference<User>[] author = new AtomicReference[]{new AtomicReference<>()};
        final String[] reason = {"No reason provided"};
        OffsetDateTime before = event.getOldTimeOutEnd();
        OffsetDateTime after  = event.getNewTimeOutEnd();

        User target = event.getUser();
        long targetId = target.getIdLong();

        guild.retrieveAuditLogs()
                .type(ActionType.MEMBER_UPDATE)
                .limit(1)
                .queue(logs -> {
                    for (AuditLogEntry entry : logs) {
                        if (entry.getTargetIdLong() == targetId) {
                            reason[0] = entry.getReason() != null
                                    ? entry.getReason()
                                    : reason[0];
                            author[0].set(entry.getUser());
                            break;
                        }
                    }
                });

        if (before == null && after != null) {
            title = "Suspended";
            content = "Reason: " + reason[0];
        }  else {
            title = "Unsuspended";
            content = " ";
        }

        MessageBuilder messageBuilder = new MessageBuilder();
        Director director = new Director(messageBuilder);
        director.makeMemberUpdate(title, content, guild, target, author[0].get());

    }

    @Override
    public Class<GuildMemberUpdateTimeOutEvent> supports() {
        return GuildMemberUpdateTimeOutEvent.class;
    }
}
