package event.events;

import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;

public final class InviteCreateEventHandler implements EventHandler<GuildInviteCreateEvent> {
    @Override
    public void handleEvent(GuildInviteCreateEvent event) {
        Guild guild;
        String title;
        String content;

        guild = event.getGuild();
        Invite invite = event.getInvite();
        User user = invite.getInviter();
        Channel channel = event.getChannel();

        String uses = (invite.getMaxUses() != 0 ? String.valueOf(invite.getMaxUses()) : "∞");
        int seconds = invite.getMaxAge();
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;

        title = "Invitation created (" + invite.getCode() + ")";
        content = "> Invitation created for: " + channel.getAsMention() + " (" + channel.getId() + ")\n" +
                "> From : " + user.getAsMention() + " (" + user.getId() + ")\n" +
                "> Duration: " + hours + "h " + minutes + "m\n" +
               "> Number of uses: " + uses + "\n" +
               "> Invitation link: https://discord.gg/" + invite.getCode();

        MessageBuilder messageBuilder = new MessageBuilder();
        Director director = new Director(messageBuilder);
        director.makeInviteCreate(title, content, guild, user);
    }

    @Override
    public Class<GuildInviteCreateEvent> supports() {
        return GuildInviteCreateEvent.class;
    }
}
