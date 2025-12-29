package systemtickets.view;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;

public final class TicketCloseView {

    public static final String CLOSE_PREFIX = "ticket:close:";

    private TicketCloseView() {}

    public static Button buildCloseButton(String pKey) {
        return Button.danger(CLOSE_PREFIX + pKey, "Close Ticket");
    }

    public static ActionRow rowOfClose(String pKey) {
        return ActionRow.of(buildCloseButton(pKey));
    }
}
