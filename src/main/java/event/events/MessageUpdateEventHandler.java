package event.events;

import BotApplication.BotApplication;
import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;

public final class MessageUpdateEventHandler implements EventHandler<MessageUpdateEvent> {
    @Override
    public void handleEvent(MessageUpdateEvent event) {
        Guild guild;

        guild = event.getGuild();
        Message oldMessage = BotApplication.removeMessage(event.getMessageIdLong());
        Message newMessage = event.getMessage();
        BotApplication.addMessage(newMessage);

        User author = newMessage.getAuthor();

        if (oldMessage == null) {
            return;
        }

        MessageBuilder messageBuilder = new MessageBuilder();
        Director director = new Director(messageBuilder);
        director.makeUpdateMessage(newMessage.getContentRaw(), oldMessage.getContentRaw(), newMessage.getChannel().getAsMention(), guild, author);
    }

    @Override
    public Class<MessageUpdateEvent> supports() {
        return MessageUpdateEvent.class;
    }
}
