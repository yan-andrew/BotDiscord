package messenger.build;

import messenger.messaging.Attached;
import messenger.messaging.Embed;
import messenger.messaging.Message;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.awt.Color;
import java.io.File;
import java.time.OffsetDateTime;

public class MessageBuilder implements Builder{

    private Class<? extends Message> messageType;
    private Message result;

    public void setType(String pType) {
        if (pType == null) {
            throw new IllegalArgumentException("Type message no valid: null");
        }

        switch (pType.trim().toUpperCase()) {
            case "EMBED":
                this.messageType = messenger.messaging.Embed.class;
                break;

            case "ATTACHED":
                this.messageType = messenger.messaging.Attached.class;
                break;

            case "MESSAGE":
                this.messageType = messenger.messaging.Message.class;
                break;

            default:
                throw new IllegalArgumentException("Type message no valid: " + pType);
        }
    }

    public void reset() {
        if (this.messageType == null) {
            throw new IllegalStateException("Message type not set. Call setType() before reset().");
        }

        try {
            // Create a fresh instance every time reset is called
            this.result = this.messageType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot instantiate message type: " + this.messageType.getName(), e);
        }
    }

    public void buildTitle(String title) {
        if (result == null) {
            throw new IllegalStateException("Call reset() before building message fields.");
        }
        result.setTitle(title);
    }

    public void buildContent(String content) {
        if (result == null) {
            throw new IllegalStateException("Call reset() before building message fields.");
        }
        result.setContent(content);
    }

    public void buildChannel(MessageChannel channel) {
        if (result == null) {
            throw new IllegalStateException("Call reset() before building message fields.");
        }
        result.setChannel(channel);
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

    public void buildTimestamp(OffsetDateTime pTime) {
        if (result instanceof Embed embed) {
            long epoch = pTime.toEpochSecond();

            String discordTime = "<t:" + epoch + ":F>";
            String discordRelative = "<t:" + epoch + ":R>";

            embed.setTimestamp("\nDate: " + discordTime + " (" + discordRelative + ")");
        }
    }

    public void buildThumbnail(String pUrl) {
        if (result instanceof Embed embed) {
            embed.setThumbnail(pUrl);
        }
    }

    public void buildFooter(String pUrl, String pTag) {
        if (result instanceof Embed embed) {
            embed.setFooterTitle(pTag);
            embed.setFooterIcon(pUrl);
        }
    }

    public Message getResult() {
        if (result == null) {
            throw new IllegalStateException("Message not built yet. Call reset() and build steps first.");
        }
        return result;
    }
}