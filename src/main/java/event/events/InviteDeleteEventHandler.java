package event.events;

import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;

public final class InviteDeleteEventHandler implements EventHandler<GuildInviteDeleteEvent> {
    @Override
    public void handleEvent(GuildInviteDeleteEvent event) {
        Guild guild;
        User user = null;
        String title, content;

        guild = event.getGuild();
        Channel channel = event.getChannel();

        title = "Invitation delete (" + event.getCode() + ")";
        content = "Invitation created for:\n" + "> " + channel.getAsMention() + " (" +
               channel.getId() + ")";

        try {
            java.util.List<AuditLogEntry> logs = guild.retrieveAuditLogs()
                    .type(ActionType.INVITE_DELETE)
                    .limit(1)
                    .complete();

            for (AuditLogEntry entry : logs) {
                if (entry.getChangeByKey("Code").getOldValue().equals(event.getCode())) {
                    user = entry.getUser();
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        MessageBuilder messageBuilder = new MessageBuilder();
        Director director = new Director(messageBuilder);
        director.makeInviteDelete(title, content, guild, user);
    }

    @Override
    public Class<GuildInviteDeleteEvent> supports() {
        return GuildInviteDeleteEvent.class;
    }
}
