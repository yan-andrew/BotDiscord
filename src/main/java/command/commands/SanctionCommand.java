package command.commands;

import command.core.BotCommand;
import command.core.CommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SanctionCommand implements BotCommand {

    @Override public String id() {
        return "saction";
    }

    @Override
    public void execute(CommandContext pCommandContext) {
        Guild guild;
        String reason, type;
        User user;

        type = pCommandContext.invokedName();

        if (type.equals("saction")) {
            pCommandContext.reply("Use one of the following commands to apply a penalty : ban, kick, mute", true);
        }

        user = Objects.requireNonNull(pCommandContext.event().getOption("user")).getAsUser();
        reason = Objects.requireNonNull(pCommandContext.event().getOption("reason")).getAsString();
        guild = pCommandContext.event().getGuild();

        if (reason.isEmpty()) {
            reason = "No reason is provided.";
        }

        if (type.equals("kick")) {
            kick(user, reason, guild);
        } else if (type.equals("mute")) {
            mute(user, reason, guild);
        } else {
            ban(user, reason, guild);
        }

        pCommandContext.reply("Sanctioned user." , true);
    }

    private void kick(User pUser, String pReason, Guild pGuild) {
        pGuild.kick(pUser).reason(pReason).queue();
    }

    private void mute(User pUser, String pReason, Guild pGuild) {
        pGuild.timeoutFor(pUser, Duration.ofMinutes(60))
                .reason(pReason)
                .queue();
    }

    private void ban(User pUser, String pReason, Guild pGuild) {
        pGuild.ban(pUser, 0, TimeUnit.DAYS)
                .reason(pReason)
                .queue();
    }
}