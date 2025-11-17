package messenger.messaging;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class Message {
    protected String title = "";
    protected String content;
    protected MessageChannel channel;

    public Message() {

    }

    public void setTitle(String pTitle) {
        title = pTitle;
    }

    public void setContent(String pContent) {
        content = pContent;
    }

    public void setChannel(MessageChannel pChannel) {
        channel = pChannel;
    }

    protected String buildText() {
        return "## " + title + "\n\n" + content;
    }

    public void sendMessage() {
        channel.sendMessage(buildText()).queue();
    }
}
