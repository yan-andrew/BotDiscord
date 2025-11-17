package messenger.messaging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;

import java.awt.Color;

public final class Embed extends Message {
    private Color color;
    private ActionRow view;

    public Embed() {

    }

    public void setColor(Color pColor) {
        color = pColor;
    }

    public void setView(ActionRow pView) {
        view = pView;
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
