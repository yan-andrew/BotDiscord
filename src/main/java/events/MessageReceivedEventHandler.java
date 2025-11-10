// File: events/handlers/MessageReceivedEventHandler.java
package events;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Handles new incoming messages.
 */
public final class MessageReceivedEventHandler implements EventHandler<MessageReceivedEvent> {

    @Override
    public void handleEvent(MessageReceivedEvent event) {
        User author = event.getAuthor();
        if (author.isBot() || author.isSystem()) {
            return;
        }

        System.out.println(
                "Message received in #" + event.getChannel().getName() +
                        " from " + author.getAsTag() +
                        ": " + event.getMessage().getContentRaw()
        );
    }

    @Override
    public Class<MessageReceivedEvent> supports() {
        return MessageReceivedEvent.class;
    }
}
