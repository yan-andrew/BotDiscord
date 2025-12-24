package messenger.build;

import messenger.messaging.Message;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import access.data.OperationSearch;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

public class Director {
    private Builder builder;

    public Director(Builder pBuilder) {
        builder = pBuilder;
    }

    public void makeBan(User pUser, String pReason, User moderator, Guild pGuild) {
        String title = "BAN";
        String content = "User: " + pUser.getAsMention() + " (" + pUser.getId() + ")\n\n"
                + "Reason: " + pReason;
        String idServer = pGuild.getId();
        String urlUserBan = pUser.getAvatarUrl();
        OffsetDateTime nowCR = OffsetDateTime.now(ZoneId.of("America/Costa_Rica"));


        Optional<String> logChannel = OperationSearch.findChannelIdForServer(idServer, "modslogs");
        String channelId = logChannel.get();

        if (channelId.isEmpty()) {
            return;
        }

        String footerText = "Sanctioned by: " +
                (moderator != null ? moderator.getAsTag() : "Unknown");

        String footerIcon =
                moderator != null ? moderator.getEffectiveAvatarUrl() : null;

        MessageChannel channel = pGuild.getChannelById(GuildMessageChannel.class, channelId);

        builder.setType("embed");
        builder.reset();
        builder.buildTitle(title);
        builder.buildContent(content);
        builder.buildTimestamp(nowCR);
        builder.buildThumbnail(urlUserBan);
        builder.buildFooter(footerIcon, footerText);
        builder.buildChannel(channel);
        builder.buildColor(Color.RED);

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeJoinVoiceChannel(String pContent, Guild pGuild, User pUser) {
        String title = "Join Voice Channel";
        String idServer = pGuild.getId();
        String urlUser = pUser.getAvatarUrl();
        OffsetDateTime nowCR = OffsetDateTime.now(ZoneId.of("America/Costa_Rica"));


        Optional<String> logChannel = OperationSearch.findChannelIdForServer(idServer, "modslogs");
        String channelId = logChannel.get();

        if (channelId.isEmpty()) {
            return;
        }

        MessageChannel channel = pGuild.getChannelById(GuildMessageChannel.class, channelId);

        builder.setType("embed");
        builder.reset();
        builder.buildTitle(title);
        builder.buildContent(pContent);
        builder.buildTimestamp(nowCR);
        builder.buildThumbnail(urlUser);
        builder.buildChannel(channel);
        builder.buildColor(Color.BLUE);

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeLeaveVoiceChannel(String pContent, Guild pGuild, User pUser) {
        String title = "Leave Voice Channel";
        String idServer = pGuild.getId();
        String urlUser = pUser.getAvatarUrl();
        OffsetDateTime nowCR = OffsetDateTime.now(ZoneId.of("America/Costa_Rica"));


        Optional<String> logChannel = OperationSearch.findChannelIdForServer(idServer, "modslogs");
        String channelId = logChannel.get();

        if (channelId.isEmpty()) {
            return;
        }

        MessageChannel channel = pGuild.getChannelById(GuildMessageChannel.class, channelId);

        builder.setType("embed");
        builder.reset();
        builder.buildTitle(title);
        builder.buildContent(pContent);
        builder.buildTimestamp(nowCR);
        builder.buildThumbnail(urlUser);
        builder.buildChannel(channel);
        builder.buildColor(Color.YELLOW);

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeCustom(String pTitle, String pContent, MessageChannel pChannel) {
        builder.setType("message");
        builder.reset();
        builder.buildTitle(pTitle);
        builder.buildContent(pContent);
        builder.buildChannel(pChannel);

        Message result = builder.getResult();
        result.sendMessage();
    }
}
