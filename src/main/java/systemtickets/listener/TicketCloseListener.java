package systemtickets.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import systemtickets.service.CloseTickets;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class TicketCloseListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        String componentId = event.getComponentId();

        if (!componentId.startsWith("ticket:close:")) {
            return;
        }

        String key = componentId.replace("ticket:close:", "");

        event.reply("Ticket closing in process.")
                .setEphemeral(true)
                .queue();

        try {
            CloseTickets.closeTicket(key, event.getChannel(), event.getInteraction().getUser() , event.getGuild());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

