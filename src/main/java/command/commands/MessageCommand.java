package command.commands;

import access.creational.ConexionDBSingleton;
import access.data.OperationSearch;
import command.core.BotCommand;
import command.core.CommandContext;
import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.Event;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MessageCommand implements BotCommand {

    @Override public String id() {
        return "message";
    }

    @Override
    public void execute(CommandContext pCommandContext) throws ExecutionException, InterruptedException {
        long channelId;
        Guild guild;
        String message = null;
        Attachment file;
        guild = pCommandContext.guild();
        Event event = pCommandContext.event();
        channelId = pCommandContext.channelId();

        if (!OperationSearch.verifyAdministrator(guild, pCommandContext.userId())) {
            pCommandContext.reply("It does not have administrative privileges." , true);
            return;
        }

        message = Objects.requireNonNull(pCommandContext.event().getOption("message")).getAsString();
        try {
            file = pCommandContext.event().getOption("file").getAsAttachment();
        } catch (Exception e) {
            file = null;
        }
        MessageChannel channel = guild.getTextChannelById(channelId);

        MessageBuilder messageBuilder = new MessageBuilder();
        Director director = new Director(messageBuilder);

        director.makeCustom("", message, channel, file);

        pCommandContext.reply("Message sent successfully." , true);
    }
}