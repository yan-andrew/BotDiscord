package command.builtin;

import command.core.CommandMetadata;
import command.core.CommandMetadataIndex;
import command.core.OptionMetadata;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class SlashPublisher {
    public CommandData toCommandData(CommandMetadata pM, String alias) {
        SlashCommandData data = Commands.slash(alias, pM.description());

        if (pM.options() != null) {
            for (OptionMetadata opt : pM.options()) {
                OptionType type = OptionType.valueOf(opt.type().toUpperCase());
                data.addOption(
                        type,
                        opt.name(),
                        opt.description(),
                        opt.required()
                );
            }
        }
        return data;
    }

    public void publishAll(JDA jda, CommandMetadataIndex metaIndex) {
        CommandListUpdateAction action = jda.updateCommands();

        for (var m : metaIndex.all()) {
            if (!m.enabled()) {
                continue;
            }

            CommandData data = toCommandData(m, m.name());
            action.addCommands(data);

            if (m.aliases() != null) {
                for (String alias : m.aliases()) {
                    if (alias == null || alias.isBlank()) {
                        continue;
                    }
                    CommandData aliasData = toCommandData(m, alias);
                    action.addCommands(aliasData);
                }
            }
        }

        action.queue();
    }
}