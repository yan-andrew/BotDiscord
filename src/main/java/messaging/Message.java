package messaging;

import io.netty.handler.pcap.PcapWriteHandler;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class Message {
    protected String title = "";
    protected String content;
    protected MessageChannel channel;

    public Message(String pContent, MessageChannel pChannel) {
        content = pContent;
        channel = pChannel;
    }

    public Message(String pTitle, String pContent, MessageChannel pChannel) {
        this (pContent, pChannel);
        title = pTitle;
    }

    protected String buildText() {
        return "## " + title + "\n\n" + content;
    }

    public void sendMessage() {
        channel.sendMessage(buildText()).queue();
    }
}
