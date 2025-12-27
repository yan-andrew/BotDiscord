package event.events;

import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

public final class GuildMemberRemoveEventHandler implements EventHandler<GuildMemberRemoveEvent> {
    @Override
    public void handleEvent(GuildMemberRemoveEvent event) {
        Guild guild;
        String reason;
        User user, author;

        MessageBuilder messageBuilder = new MessageBuilder();
        Director director = new Director(messageBuilder);

        guild = event.getGuild();
        user = event.getUser();
        author = null;
        reason = "No reason provided";

        try {
            java.util.List<AuditLogEntry> logs = guild.retrieveAuditLogs()
                    .type(ActionType.KICK)
                    .limit(1)
                    .complete();

            for (AuditLogEntry entry : logs) {
                if (entry.getTargetIdLong() == user.getIdLong()) {
                    author = entry.getUser();
                    reason = entry.getReason();
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (author == null) {
            director.makeMemberRemove("Member left the server", "> User: " + user.getAsMention()
                    + " (" + user.getId() + ")", guild, user);
            return;
        }

        director.makeKick(user, reason, author, guild);
    }

    @Override
    public Class<GuildMemberRemoveEvent> supports() {
        return GuildMemberRemoveEvent.class;
    }
}
