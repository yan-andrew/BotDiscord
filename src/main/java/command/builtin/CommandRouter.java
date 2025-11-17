package command.builtin;

import command.core.CommandContext;
import command.core.CommandMetadataIndex;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandRouter extends ListenerAdapter {
    private final CommandRegistry behavior;
    private final CommandMetadataIndex metaIndex;

    public CommandRouter(CommandRegistry behavior, CommandMetadataIndex metaIndex) {
        this.behavior = behavior;
        this.metaIndex = metaIndex;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String slash = event.getName(); // the alias or main name used
        var idOpt = metaIndex.idBySlashName(slash);
        if (idOpt.isEmpty()) {
            event.reply("Unknown command.").setEphemeral(true).queue();
            return;
        }
        String id = idOpt.get();

        var metaOpt = metaIndex.byId(id);
        if (metaOpt.isPresent() && !metaOpt.get().enabled()) {
            event.reply("This command is currently disabled.").setEphemeral(true).queue();
            return;
        }

        var cmdOpt = behavior.findById(id);
        if (cmdOpt.isEmpty()) {
            event.reply("Command behavior not found for id: " + id).setEphemeral(true).queue();
            return;
        }

        try {
            // pass invokedName to the context
            cmdOpt.get().execute(new CommandContext(event, slash));
        } catch (Exception ex) {
            event.reply("Internal error while executing the command.").setEphemeral(true).queue();
            ex.printStackTrace();
        }
    }
}