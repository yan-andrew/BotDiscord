package event.events;

import BotApplication.BotApplication;
import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;

public final class MessageDeleteEventHandler implements EventHandler<MessageDeleteEvent> {
    @Override
    public void handleEvent(MessageDeleteEvent event) {
        Guild guild;

        guild = event.getGuild();
        guild.retrieveAuditLogs()
                .type(ActionType.MESSAGE_DELETE)
                .limit(1)
                .queue(logs -> {
                    Message message = BotApplication.removeMessage(event.getMessageIdLong());
                    User author = null;
                    User user = message.getAuthor();

                    if (message == null) {
                        return;
                    }

                    for (AuditLogEntry entry : logs) {
                        if (entry.getTargetIdLong() == user.getIdLong()) {
                            author = entry.getUser();
                            break;
                        }
                    }

                    MessageBuilder messageBuilder = new MessageBuilder();
                    Director director = new Director(messageBuilder);
                    director.makeDeleteMessage(message.getContentRaw(), message.getChannel().getAsMention(), guild, user, author);
                });
    }

    @Override
    public Class<MessageDeleteEvent> supports() {
        return MessageDeleteEvent.class;
    }
}
