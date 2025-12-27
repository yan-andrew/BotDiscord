package command.commands;

import command.core.BotCommand;
import command.core.CommandContext;
import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;

import java.util.Objects;

public class AdvertisementCommand implements BotCommand {

    @Override public String id() {
        return "ads";
    }

    @Override
    public void execute(CommandContext pCommandContext) {
        Guild guild;
        String title, content;
        Event event = pCommandContext.event();
        guild = pCommandContext.guild();

        MessageBuilder messageBuilder = new MessageBuilder();
        Director director = new Director(messageBuilder);

        title = Objects.requireNonNull(pCommandContext.event().getOption("title")).getAsString();
        content = Objects.requireNonNull(pCommandContext.event().getOption("content")).getAsString();

        director.makeAdvertisement(title, content, guild);

        pCommandContext.reply("Message sent successfully." , true);
    }
}