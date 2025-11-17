package messaging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.awt.Color;

public final class Embed extends Message {
    private Color color;
    private ActionRow View;

    public Embed(String pTitle, String pContent, MessageChannel pChannel, Color pColor) {
        super(pTitle, pContent, pChannel);
        color = pColor;
    }

    public Embed(String pTitle, String pContent, MessageChannel pChannel, Color pColor, ActionRow pView) {
        this(pTitle, pContent, pChannel, pColor);
        View = pView;
    }

    @Override
    public void sendMessage() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(title);
        embed.setColor(color);
        embed.setDescription(content);

        channel.sendMessageEmbeds(embed.build()).queue();
    }
}
