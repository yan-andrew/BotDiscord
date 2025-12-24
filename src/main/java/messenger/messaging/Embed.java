package messenger.messaging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;

import java.awt.Color;
import java.time.OffsetDateTime;

public final class Embed extends Message {
    private Color color;
    private ActionRow view;
    private String time;
    private String thumbnailUrl;
    private String footerIcon;
    private String footerTitle;

    public Embed() {

    }

    public void setColor(Color pColor) {
        color = pColor;
    }

    public void setView(ActionRow pView) {
        view = pView;
    }

    public void setTimestamp(String pTime) {
        time = pTime;
    }

    public void setThumbnail(String pUrl) {
        thumbnailUrl = pUrl;
    }

    public void setFooterTitle(String pTitle) {
        footerTitle = pTitle;
    }

    public void setFooterIcon(String pIcon) {
        footerIcon = pIcon;
    }

    @Override
    public void sendMessage() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(title);
        embed.setColor(color);
        embed.setDescription(content + "\n\n" + time);
        embed.setFooter(footerTitle, footerIcon);
        embed.setThumbnail(thumbnailUrl);

        channel.sendMessageEmbeds(embed.build()).queue();
    }
}
