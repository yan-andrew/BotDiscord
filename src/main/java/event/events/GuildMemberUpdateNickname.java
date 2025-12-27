package event.events;

import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;

public final class GuildMemberUpdateNickname implements EventHandler<GuildMemberUpdateNicknameEvent> {
    @Override
    public void handleEvent(GuildMemberUpdateNicknameEvent event) {
        Guild guild = event.getGuild();

        guild.retrieveAuditLogs()
                .type(ActionType.MEMBER_UPDATE)
                .limit(1)
                .queue(logs -> {
                    String before = event.getOldNickname();
                    String after  = event.getNewNickname();
                    User author = null;

                    User target = event.getUser();
                    long targetId = target.getIdLong();

                    for (AuditLogEntry entry : logs) {
                        if (entry.getTargetIdLong() == targetId) {
                            author = entry.getUser();
                            break;
                        }
                    }

                    MessageBuilder messageBuilder = new MessageBuilder();
                    Director director = new Director(messageBuilder);

                    if (before == null) {
                        before = target.getName();
                    }
                    if (after == null) {
                        after = target.getName();
                    }

                    director.makeMemberUpdate("Nickname change for " + target.getName(), before + " was changed to " + after,
                            guild, target, author);
                });
    }


    @Override
    public Class<GuildMemberUpdateNicknameEvent> supports() {
        return GuildMemberUpdateNicknameEvent.class;
    }
}
