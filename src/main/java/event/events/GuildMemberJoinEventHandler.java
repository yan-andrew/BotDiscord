package event.events;

import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

public final class GuildMemberJoinEventHandler implements EventHandler<GuildMemberJoinEvent> {
    @Override
    public void handleEvent(GuildMemberJoinEvent event) {
        Guild guild;
        String title;
        String content;
        User user;

        guild = event.getGuild();
        user = event.getUser();

        title = "New member: " + user.getName();
        content = "> User :" + user.getAsMention() + " (" + user.getId() + ")";

        MessageBuilder messageBuilder = new MessageBuilder();
        Director director = new Director(messageBuilder);
        director.makeMemberJoin(title, content, guild, user);
    }

    @Override
    public Class<GuildMemberJoinEvent> supports() {
        return GuildMemberJoinEvent.class;
    }
}
