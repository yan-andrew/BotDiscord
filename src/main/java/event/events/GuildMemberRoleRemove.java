package event.events;

import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;

public final class GuildMemberRoleRemove implements EventHandler<GuildMemberRoleRemoveEvent> {
    @Override
    public void handleEvent(GuildMemberRoleRemoveEvent event) {
        Guild guild = event.getGuild();

        guild.retrieveAuditLogs()
                .type(ActionType.MEMBER_ROLE_UPDATE)
                .limit(15)
                .queue(logs -> {
                    String content;
                    User author = null;

                    User target = event.getUser();
                    long targetId = target.getIdLong();

                    for (AuditLogEntry entry : logs) {
                        if (entry.getTargetIdLong() == targetId) {
                            author = entry.getUser();
                            break;
                        }
                    }

                    content = "Role(s) remove for " + target.getAsMention() + ": \n";

                    for (Role role : event.getRoles()) {
                        content += "> " + role.getAsMention() + "\n";
                    }

                    MessageBuilder messageBuilder = new MessageBuilder();
                    Director director = new Director(messageBuilder);
                    director.makeMemberUpdate("❌  Role removed", content, guild, target, author);
                });
    }


    @Override
    public Class<GuildMemberRoleRemoveEvent> supports() {
        return GuildMemberRoleRemoveEvent.class;
    }
}
