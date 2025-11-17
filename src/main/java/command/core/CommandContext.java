package command.core;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandContext {
    private final SlashCommandInteractionEvent event;
    private final String invokedName; // slash name actually used (alias or main)

    public CommandContext(SlashCommandInteractionEvent event, String invokedName) {
        this.event = event;
        this.invokedName = invokedName;
    }

    public SlashCommandInteractionEvent event() {
        return event;
    }

    public String invokedName() {
        return invokedName;
    }

    public long guildId() {
        Guild g = event.getGuild();
        return (g == null) ? 0L : g.getIdLong();
    }

    public long channelId() {
        return event.getChannel().getIdLong();
    }

    public long userId() {
        return event.getUser().getIdLong();
    }

    public void reply(String message, boolean ephemeral) {
        event.reply(message).setEphemeral(ephemeral).queue();
    }
}