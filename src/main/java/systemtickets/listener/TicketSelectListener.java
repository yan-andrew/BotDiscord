package systemtickets.listener;

import access.data.OperationSearch;
import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import systemtickets.view.TicketCloseView;

import java.util.EnumSet;
import java.util.Optional;

public class TicketSelectListener extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("ticket:select")) return;

        Member member = event.getMember();
        if (member == null || event.getGuild() == null) return;
        Guild guild = member.getGuild();

        String typeId = event.getValues().get(0);
        String userId = member.getId();
        String message = "Ticket created. Use the button below to close it.";

        guild.createTextChannel("ticket-" + userId)
                .addPermissionOverride(
                        guild.getPublicRole(),
                        null,
                        EnumSet.of(
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_SEND,
                                Permission.MESSAGE_HISTORY
                        )
                )

                .addPermissionOverride(
                        member,
                        EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION),
                        null
                )
                .queue(channel -> {
                    applyTicketPermissions(guild, typeId, channel);

                    MessageBuilder messageBuilder = new MessageBuilder();
                    Director director = new Director(messageBuilder);
                    director.makeView(message, guild, TicketCloseView.rowOfClose(channel.getId()), channel);

                    event.reply("Ticket created successfully. (" + channel.getAsMention() + ")")
                            .setEphemeral(true)
                            .queue();
                });
    }

    private void applyTicketPermissions(Guild guild, String typeId, TextChannel channel) {
        Optional<String> minRoleIdOpt = OperationSearch.getRolesByTicketType(guild.getIdLong(), typeId);

        if (minRoleIdOpt.isEmpty()) {
            return;
        }

        Role minRole = guild.getRoleById(minRoleIdOpt.get());
        if (minRole == null) {
            return;
        }

        int minPos = minRole.getPositionRaw();

        for (Role role : guild.getRoles()) {

            if (role.isPublicRole() || role.isManaged()) continue;

            if (role.getPositionRaw() >= minPos) {
                channel.upsertPermissionOverride(role)
                        .setAllowed(EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY, Permission.MESSAGE_SEND, Permission.MESSAGE_ADD_REACTION))
                        .queue();
            }
        }
    }
}
