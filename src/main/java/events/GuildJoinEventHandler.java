package events;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

/**
 * Handles when the bot joins a new guild.
 */
public final class GuildJoinEventHandler implements EventHandler<GuildJoinEvent> {
    @Override
    public void handleEvent(GuildJoinEvent event) {
        String name = event.getGuild().getName();
        String id = event.getGuild().getId();

        System.out.println("Joined a new guild: " + name + " (ID: " + id + ")");
    }

    @Override
    public Class<GuildJoinEvent> supports() {
        return GuildJoinEvent.class;
    }
}
