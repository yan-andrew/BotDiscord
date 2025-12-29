package command.commands;

import command.core.BotCommand;
import command.core.CommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.time.Duration;
import java.util.Objects;

public class UnSanctionCommand implements BotCommand {

    @Override public String id() {
        return "saction";
    }

    @Override
    public void execute(CommandContext pCommandContext) {
        Guild guild;
        String reason, type;
        User user;

        type = pCommandContext.invokedName();

        if (type.equals("unsaction")) {
            pCommandContext.reply("Use one of the following commands to remove a sanction.", true);
        }

        user = Objects.requireNonNull(pCommandContext.event().getOption("user")).getAsUser();
        guild = pCommandContext.event().getGuild();

        if (type.equals("unmute")) {
            unMute(user, guild);
        } else {
            unBan(user, guild);
        }

        pCommandContext.reply("Unsanctioned user." , true);
    }

    private void unMute(User pUser, Guild pGuild) {
        pGuild.timeoutFor(pUser, Duration.ofMinutes(0))
                .queue();
    }

    private void unBan(User pUser, Guild pGuild) {
        pGuild.unban(pUser)
                .queue();
    }
}