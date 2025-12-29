package command.commands;

import access.creational.ConexionDBSingleton;
import access.data.OperationSearch;
import command.core.CommandContext;
import command.core.BotCommand;
import messenger.build.Director;
import messenger.build.MessageBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.Event;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;
import systemtickets.view.TicketPanelView;
import systemtickets.viewmodel.TicketTypeVM;

import java.util.ArrayList;

public class RegisterChannelCommand implements BotCommand {

    @Override public String id() {
        return "channel.register";
    }

    @Override
    public void execute(CommandContext pCommandContext) {
        Guild guild;
        String typeRegister, activity, channelId = null;
        guild = pCommandContext.guild();
        Event event = pCommandContext.event();

        if (!OperationSearch.verifyAdministrator(guild, pCommandContext.userId())) {
            pCommandContext.reply("It does not have administrative privileges." , true);
            return;
        }


        Channel channel = pCommandContext.event().getOption("channel").getAsChannel();

        typeRegister = pCommandContext.invokedName();
        typeRegister = typeRegister.replaceAll("(?i)\\s*-channel$", "");

        if (typeRegister.equals("channel-register")) {
            pCommandContext.reply("Use the commands modslogs-channel, vcg-channel, " +
                    "tickets-channel, advertising-channel, " +
                    "ticketlogs-channel to register the respective channels." , true);
            return;
        }

        activity = registerChannel(channel, typeRegister, guild);

        if (activity == null) {
            pCommandContext.reply("Unsupported alias for registration.", true);
            return;
        }

        pCommandContext.reply("Registered channel for activity '" + activity + "' successfully.", true);
    }

    private String registerChannel(Channel pChannel, String pTypeRegister, Guild pGuild) {
        String result;
        String cypher =
                "MATCH (s:Server {id: $id}) " +
                        "MERGE (c:Channel {channelId: $channelId}) " +
                        "MERGE (s)-[r:USES_CHANNEL {type: $type}]->(c) " +
                        "RETURN s, c, r";

        var conexion = ConexionDBSingleton.getInstance();

        try (Session session = conexion.newSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                transaction.run(cypher, Values.parameters("id", pGuild.getId(),
                        "channelId", pChannel.getId(), "type", pTypeRegister));
                transaction.commit();
                result = pTypeRegister;
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            } finally {
                transaction.close();
            }
        }

        if (pTypeRegister.equals("tickets")) {
            if (!registerTicket(pChannel, pGuild)) {
                result = "You must register the ticket types first, use /addtypeticket";
            }
        }

        return result;
    }

    private boolean registerTicket(Channel pChannel, Guild pGuild) {

        if (!(pChannel instanceof TextChannel textChannel)) {
            return true;
        }

        long serverId = textChannel.getGuild().getIdLong();

        ArrayList<TicketTypeVM> ticketTypes =
                OperationSearch.getTicketTypesForPanel(serverId);

        StringSelectMenu menu = TicketPanelView.buildSelectMenu(
                ticketTypes,
                "Select a ticket type"
        );

        ActionRow row = TicketPanelView.rowOf(menu);

        if (row == null) {
            return false;
        }

        MessageBuilder messageBuilder = new MessageBuilder();
        Director director = new Director(messageBuilder);
        director.makeView("To open a ticket, select an option from the menu below.", pGuild, row, pChannel);

        return true;
    }
}