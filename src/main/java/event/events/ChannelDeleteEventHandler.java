package event.events;

import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;

public final class ChannelDeleteEventHandler implements EventHandler<ChannelDeleteEvent> {
    @Override
    public void handleEvent(ChannelDeleteEvent event) {
        Guild guild;
        guild = event.getGuild();
        User user = null;
        Channel channel = event.getChannel();
        String channelName, channelId;

        try {
            java.util.List<AuditLogEntry> logs = guild.retrieveAuditLogs()
                    .type(ActionType.CHANNEL_DELETE)
                    .limit(1)
                    .complete();

            for (AuditLogEntry entry : logs) {
                if (entry.getTargetIdLong() == channel.getIdLong()) {
                    user = entry.getUser();
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channelName = channel.getName();
            channelId = channel.getId();

            MessageBuilder messageBuilder = new MessageBuilder();
            Director director = new Director(messageBuilder);
            director.makeChannelDelete(channelName, channelId, guild, user);
        }
    }

    @Override
    public Class<ChannelDeleteEvent> supports() {
        return ChannelDeleteEvent.class;
    }
}
