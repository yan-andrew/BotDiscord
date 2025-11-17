package messenger.build;

import messenger.messaging.Attached;
import messenger.messaging.Embed;
import messenger.messaging.Message;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.awt.Color;
import java.io.File;

public final class MessageBuilder implements Builder {
    private Message result;
    private Class<? extends Message> messageType = Message.class;

    public void setType(String pType) {
        try {
            Class<?> clazz = Class.forName(pType);
            this.messageType = clazz.asSubclass(Message.class);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Type message no valid: " + pType, e);
        }
    }

    @Override
    public void reset() {
        try {
            result = messageType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error created instance: " + messageType.getName(), e);
        }
    }

    @Override
    public void buildTitle(String pTitle) {
        result.setTitle(pTitle);
    }

    @Override
    public void buildContent(String pContent) {
        result.setContent(pContent);
    }

    @Override
    public void buildChannel(MessageChannel pChannel) {
        result.setChannel(pChannel);
    }

    @Override
    public void buildColor(Color pColor) {
        if (result instanceof Embed embed) {
            embed.setColor(pColor);
        }
    }

    @Override
    public void buildView(ActionRow pView) {
        if (result instanceof Embed embed) {
            embed.setView(pView);
        }
    }

    @Override
    public void buildFile(File pFile) {
        if (result instanceof Attached attached) {
            attached.setFile(pFile);
        }
    }

    public Message getResult() {
        return result;
    }

    public void sendBuiltMessage() {
        result.sendMessage();
    }
}
