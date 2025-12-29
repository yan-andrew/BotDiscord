package messenger.build;

import messenger.messaging.Message;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.Message.Attachment;
import access.data.OperationSearch;

import java.util.Random;

import java.awt.*;
import java.io.File;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class Director {
    private Builder builder;

    public Director(Builder pBuilder) {
        builder = pBuilder;
    }

    private boolean generateModlogsEmbed(String pTitle, String pMessage, Guild pGuild, String pFooterText, String pFooterIcon) {
        String idServer = pGuild.getId();
        OffsetDateTime nowCR = OffsetDateTime.now(ZoneId.of("America/Costa_Rica"));

        Optional<String> logChannel = OperationSearch.findChannelIdForServer(idServer, "modslogs");
        String channelId = logChannel.get();

        if (channelId.isEmpty()) {
            return true;
        }

        MessageChannel channel = pGuild.getChannelById(GuildMessageChannel.class, channelId);

        builder.setType("embed");
        builder.reset();
        builder.buildTitle(pTitle);
        builder.buildContent(pMessage);
        builder.buildTimestamp(nowCR);
        builder.buildFooter(pFooterIcon, pFooterText);
        builder.buildChannel(channel);
        return false;
    }

    public static File getRandomGif() {
        Random random = new Random();

        File folder = new File("decorations");

        if (!folder.exists() || !folder.isDirectory()) {
            return null;
        }

        File[] gifs = folder.listFiles(file ->
                file.isFile() && file.getName().toLowerCase().endsWith(".gif")
        );

        if (gifs == null || gifs.length == 0) {
            return null;
        }

        return gifs[random.nextInt(gifs.length)];
    }

    public void makeBan(User pUser, String pReason, User moderator, Guild pGuild) {
        String title = "BAN";
        String content = "> User: " + pUser.getAsMention() + " (" + pUser.getId() + ")\n"
                + "> Reason: " + pReason;

        String footerText = "Sanctioned by: " +
                (moderator != null ? moderator.getName() : "Unknown");

        String footerIcon =
                moderator != null ? moderator.getEffectiveAvatarUrl() : null;

        if (generateModlogsEmbed(title, content, pGuild, footerText, footerIcon)) return;

        builder.buildColor(Color.RED);
        builder.buildThumbnail(pUser.getAvatarUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeUnBan(User pUser, User pModerator, Guild pGuild) {
        String title = "UNBAN";
        String content = "> User: " + pUser.getAsMention() + " (" + pUser.getId();

        String footerText = "Unsanctioned by: " +
                (pModerator != null ? pModerator.getName() : "Unknown");

        String footerIcon =
                pModerator != null ? pModerator.getEffectiveAvatarUrl() : null;

        if (generateModlogsEmbed(title, content, pGuild, footerText, footerIcon)) return;

        builder.buildColor(Color.GREEN);
        builder.buildThumbnail(pUser.getAvatarUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeKick(User pUser, String pReason, User pModerator, Guild pGuild) {
        String title = "KICK";
        String content = "> User: " + pUser.getAsMention() + " (" + pUser.getId() + ")\n"
                + "> Reason: " + pReason;

        String footerText = "Kicked by: " +
                (pModerator != null ? pModerator.getName() : "Unknown");

        String footerIcon =
                pModerator != null ? pModerator.getEffectiveAvatarUrl() : null;

        if (generateModlogsEmbed(title, content, pGuild, footerText, footerIcon)) return;

        builder.buildColor(Color.LIGHT_GRAY);
        builder.buildThumbnail(pUser.getAvatarUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeAdvertisement(String pTitle, String pContent, Guild pGuild) {
        String idServer = pGuild.getId();
        File file;
        MessageChannel channel;

        Optional<String> channelAdvertising = OperationSearch.findChannelIdForServer(idServer, "advertising");
        String channelId = channelAdvertising.get();

        if (channelId.isEmpty()) {
            return;
        }

        channel = pGuild.getChannelById(GuildMessageChannel.class, channelId);
        file = getRandomGif();

        builder.setType("ATTACHED");
        builder.reset();
        builder.buildTitle(pTitle + " @everyone");
        builder.buildContent(pContent);
        builder.buildChannel(channel);
        builder.buildFile(file);

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeDeleteMessage(String pContent, String pChannel, Guild pGuild, User pUser, User pAuthor) {
        String title = "Message Deleted";
        String content = "Channel : " + pChannel + "\n"
                + "> User: " + pUser.getAsMention() + " (" + pUser.getId() + ")\n"
                + "> Content: " + pContent;

        String footerText = "Deleted by: " +
                (pAuthor != null ? pAuthor.getName() : pUser.getName());

        String footerIcon =
                pAuthor != null ? pAuthor.getEffectiveAvatarUrl() : pUser.getAvatarUrl();

        if (generateModlogsEmbed(title, content, pGuild, footerText, footerIcon)) return;
        builder.buildColor(Color.orange);
        builder.buildThumbnail(pUser.getAvatarUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeUpdateMessage(String pNewContent, String pOldContent, String pChannel,Guild pGuild, User pUser) {
        String title = "Message Updated";
        String content = "Channel: " + pChannel + "\n"
                + "> Old content: " + pOldContent + "\n\n"
                + "> New content: " + pNewContent;

        String footerText = "Author: " + pUser.getName();

        String footerIcon = pUser.getAvatarUrl();

        if (generateModlogsEmbed(title, content, pGuild, footerText, footerIcon)) return;
        builder.buildColor(Color.WHITE);
        builder.buildThumbnail(pGuild.getIconUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeMemberJoin(String pTitle, String pMessage, Guild pGuild, User pUser) {
        if (generateModlogsEmbed(pTitle, pMessage, pGuild, pGuild.getName(), pGuild.getIconUrl())) return;
        builder.buildColor(Color.GREEN);
        builder.buildThumbnail(pUser.getAvatarUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeMemberUpdate(String pTitle, String pMessage, Guild pGuild, User pUser, User pAuthor) {
        String footerText = "Change made by: " +
                (pAuthor != null ? pAuthor.getName() : "Unknown");

        String footerIcon =
                pAuthor != null ? pAuthor.getEffectiveAvatarUrl() : null;

        if (generateModlogsEmbed(pTitle, pMessage, pGuild, footerText, footerIcon)) return;
        builder.buildColor(Color.GRAY);
        builder.buildThumbnail(pUser.getAvatarUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeMemberRemove(String pTitle, String pMessage, Guild pGuild, User pUser) {
        if (generateModlogsEmbed(pTitle, pMessage, pGuild, pGuild.getName(), pGuild.getIconUrl())) return;
        builder.buildColor(Color.BLACK);
        builder.buildThumbnail(pUser.getAvatarUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeInviteCreate(String pTitle, String pMessage, Guild pGuild, User pUser) {
        if (generateModlogsEmbed(pTitle, pMessage, pGuild, pGuild.getName(), pGuild.getIconUrl())) return;
        builder.buildColor(Color.MAGENTA);
        builder.buildThumbnail(pUser.getAvatarUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeInviteDelete(String pTitle, String pMessage, Guild pGuild, User pUser) {
        String footerText = "Deleted by: " +
                (pUser != null ? pUser.getName() : "Unknown");

        String footerIcon =
                pUser != null ? pUser.getEffectiveAvatarUrl() : null;

        if (generateModlogsEmbed(pTitle, pMessage, pGuild, footerText, footerIcon)) return;
        builder.buildColor(Color.DARK_GRAY);
        builder.buildThumbnail(pGuild.getIconUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeChannelCreate(String pTitle, String pMessage, Guild pGuild, User pUser) {
        String footerText = "Created by: ";
        String footerIcon;
        String title = "Channel Created " + pTitle;
        String content = "> Channel: " + pTitle + " (" + pMessage + ")\n";

        if (pUser == null) {
            footerIcon = pGuild.getIconUrl();
            footerText = pGuild.getName();
        } else {
            footerText += pUser.getName();
            footerIcon = pUser.getAvatarUrl();
        }

        if (generateModlogsEmbed(title, content, pGuild, footerText, footerIcon)) return;
        builder.buildColor(Color.GREEN);
        builder.buildThumbnail(pGuild.getIconUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeChannelDelete(String pTitle, String pMessage, Guild pGuild, User pUser) {
        String footerText = "Delete by: ";
        String footerIcon;
        String title = "Channel delete " + pTitle;
        String content = "> Channel: " + pTitle + "\n" +
                "> ID: " + pMessage;

        if (pUser == null) {
            footerIcon = pGuild.getIconUrl();
            footerText = pGuild.getName();
        } else {
            footerText += pUser.getName();
            footerIcon = pUser.getAvatarUrl();
        }

        if (generateModlogsEmbed(title, content, pGuild, footerText, footerIcon)) return;
        builder.buildColor(Color.RED);
        builder.buildThumbnail(pGuild.getIconUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeJoinVoiceChannel(String pContent, Guild pGuild, User pUser) {
        String title = "Join Voice Channel";

        if (generateModlogsEmbed(title, pContent, pGuild, pGuild.getName(), pGuild.getIconUrl())) return;

        builder.buildColor(Color.BLUE);
        builder.buildThumbnail(pUser.getAvatarUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeLeaveVoiceChannel(String pContent, Guild pGuild, User pUser) {
        String title = "Leave Voice Channel";

        if (generateModlogsEmbed(title, pContent, pGuild, pGuild.getName(), pGuild.getIconUrl())) return;

        builder.buildColor(Color.YELLOW);
        builder.buildThumbnail(pUser.getAvatarUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeChangeVoiceChannel(String pContent, Guild pGuild, User pUser) {
        String title = "Change in voice channels";

        if (generateModlogsEmbed(title, pContent, pGuild, pGuild.getName(), pGuild.getIconUrl())) return;

        builder.buildColor(Color.cyan);
        builder.buildThumbnail(pUser.getAvatarUrl());

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeView(String pMessage, Guild pGuild, ActionRow pView, Channel pChannel) {
        builder.setType("embed");
        builder.reset();
        builder.buildTitle("Tickets System");
        builder.buildContent(pMessage);
        builder.buildFooter(pGuild.getIconUrl(), pGuild.getName());
        builder.buildChannel((MessageChannel) pChannel);
        builder.buildView(pView);
        builder.buildColor(Color.MAGENTA);

        Message result = builder.getResult();
        result.sendMessage();
    }

    public void makeTicketLog(String Title, String pMessage, Guild pGuild, File pFile, User pUser) {
        String idServer = pGuild.getId();

        Optional<String> logChannel = OperationSearch.findChannelIdForServer(idServer, "ticketlogs");
        String channelId = logChannel.get();
        String footerText = pUser.getName();
        String footerIcon = pUser.getAvatarUrl();

        if (channelId.isEmpty()) {
            return;
        }

        MessageChannel channel = pGuild.getChannelById(GuildMessageChannel.class, channelId);

        builder.setType("embed");
        builder.reset();
        builder.buildTitle(Title);
        builder.buildContent(pMessage);
        builder.buildFooter(footerIcon, "Close by: " + footerText);
        builder.buildChannel(channel);
        builder.buildColor(Color.MAGENTA);

        Message result = builder.getResult();
        result.sendMessage();

        builder.setType("ATTACHED");
        builder.reset();
        builder.buildTitle("");
        builder.buildContent("");
        builder.buildFile(pFile);
        builder.buildChannel(channel);

        result = builder.getResult();
        result.sendMessage();
    }

    public void makeCustom(String pTitle, String pContent, MessageChannel pChannel, Attachment pFile) throws ExecutionException, InterruptedException {
        if (pFile == null) {
            builder.setType("message");
        } else {
            builder.setType("ATTACHED");
        }
        builder.reset();
        builder.buildTitle(pTitle);
        builder.buildContent(pContent);
        builder.buildChannel(pChannel);
        builder.buildFile(pFile);

        Message result = builder.getResult();
        result.sendMessage();
    }
}
