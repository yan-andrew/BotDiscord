package event.events;

import access.data.OperationSearch;
import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;


public final class VoiceChannelJoinEvent implements EventHandler<GuildVoiceUpdateEvent> {
    private static final String VCS_SUFFIX = "| VCS";

    @Override
    public void handleEvent(GuildVoiceUpdateEvent pEvent) {
        Guild guild = pEvent.getGuild();
        User user = pEvent.getMember().getUser();
        AudioChannelUnion channelJoin = pEvent.getChannelJoined();
        AudioChannelUnion channelLeft = pEvent.getChannelLeft();

        if (channelJoin != null && channelLeft != null) {
            onChange(guild, user, channelLeft, channelJoin);
            return;
        } else if (channelJoin != null) {
            onJoin(guild, user, channelJoin);
        } else if (channelLeft != null) {
            onLeave(guild, user, channelLeft);
        }
    }

    @Override
    public Class<GuildVoiceUpdateEvent> supports() {
        return GuildVoiceUpdateEvent.class;
    }

    private void onJoin(Guild pGuild, User pUser, AudioChannelUnion pChannel) {
        String channelId = pChannel.getId();
        String serverId = pGuild.getId();
        VoiceChannel channel = pChannel.asVoiceChannel();
        String VCGId = OperationSearch.findChannelIdForServer(serverId, "vcg").get();

        if (channelId.equals(VCGId)) {
            VCGenerator(pGuild, pUser, channel);
        } else {
            MessageBuilder messageBuilder = new MessageBuilder();
            Director director = new Director(messageBuilder);
            String content = "> User: " + pUser.getAsMention() + " (" + pUser.getId() + ")\n" +
                    "> Join: " + channel.getName() + " (" + channel.getAsMention() + ")";

            director.makeJoinVoiceChannel(content, pGuild, pUser);
        }
    }

    private void onLeave(Guild pGuild, User pUser, AudioChannelUnion pChannel) {
        String channelId = pChannel.getId();
        String serverId = pGuild.getId();
        VoiceChannel channel = pChannel.asVoiceChannel();

        if (channel.getName().endsWith(VCS_SUFFIX)) {
            if (channel.getMembers().isEmpty()) {
                channel.delete().queue();
                return;
            }
        }

        MessageBuilder messageBuilder = new MessageBuilder();
        Director director = new Director(messageBuilder);
        String content = "> User: " + pUser.getAsMention() + " (" + pUser.getId() + ")\n\n" +
                "> Leave: " + channel.getName() + " (" + channel.getAsMention() + ")";

        director.makeLeaveVoiceChannel(content, pGuild, pUser);
    }

    private void onChange(Guild pGuild, User pUser, AudioChannelUnion pChannelLeft, AudioChannelUnion pChannelJoin) {
        String serverId = pGuild.getId();
        String channelId = pChannelJoin.getId();
        VoiceChannel channelJoin = pChannelJoin.asVoiceChannel();
        VoiceChannel channelLeft = pChannelLeft.asVoiceChannel();
        String VCGId = OperationSearch.findChannelIdForServer(serverId, "vcg").get();

        if (channelLeft.getName().endsWith(VCS_SUFFIX)) {
            if (channelLeft.getMembers().isEmpty()) {
                channelLeft.delete().queue();
                return;
            }
        }

        if (channelId.equals(VCGId)) {
            VCGenerator(pGuild, pUser, channelJoin);
        } else {MessageBuilder messageBuilder = new MessageBuilder();
            Director director = new Director(messageBuilder);

            String content = "> User: " + pUser.getAsMention() + " (" + pUser.getId() + ")\n\n" +
                    "> Move: " + pChannelLeft.getName() + " (" + pChannelLeft.getAsMention() + ") ---> "
                    + pChannelJoin.getName() + " (" + pChannelJoin.getAsMention() + ")";

            director.makeChangeVoiceChannel(content, pGuild, pUser);
        }
    }

    private void VCGenerator(Guild pGuild, User pUser, VoiceChannel pChannel) {
        Category category = pChannel.getParentCategory();

        pGuild.createVoiceChannel(
                pUser.getEffectiveName() + "'s channel " + VCS_SUFFIX,
                category
        ).queue(createdChannel -> {

            pGuild.moveVoiceMember(pUser, createdChannel).queue();
        });
    }
}
