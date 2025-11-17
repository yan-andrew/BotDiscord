package command.builtin;

import command.core.CommandMetadata;
import command.core.CommandMetadataIndex;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class SlashPublisher {
    public void publishAll(JDA jda, CommandMetadataIndex metaIndex) {
        CommandListUpdateAction action = jda.updateCommands();

        for (var m : metaIndex.all()) {
            if (!m.enabled()) {
                continue;
            }

            CommandData main = Commands.slash(m.name(), m.description());
            action.addCommands(main);

            if (m.aliases() != null) {
                for (String a : m.aliases()) {
                    if (a == null || a.isBlank()) {
                        continue;
                    }
                    CommandData aliasData = Commands.slash(a, m.description());
                    action.addCommands(aliasData);
                }
            }
        }

        action.queue();
    }
}