package systemtickets.view;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import systemtickets.viewmodel.TicketTypeVM;

import java.util.ArrayList;

public final class TicketPanelView {

    public static final String SELECT_ID = "ticket:select";

    private TicketPanelView() {}

    public static StringSelectMenu buildSelectMenu(
            ArrayList<TicketTypeVM> pType,
            String pPlaceholder
    ) {

        StringSelectMenu.Builder menu = StringSelectMenu.create(SELECT_ID)
                .setPlaceholder(pPlaceholder)
                .setMinValues(1)
                .setMaxValues(1);

        if (pType == null || pType.isEmpty()) {
            return null;
        } else {
            for (TicketTypeVM type : pType) {

                String label = resolveLabel(type);
                menu.addOption(label, type.id(), type.description(), Emoji.fromUnicode(type.emoji()));
            }
        }

        return menu.build();
    }

    public static ActionRow rowOf(StringSelectMenu pMenu) {
        return ActionRow.of(pMenu);
    }

    private static String resolveLabel(TicketTypeVM pType) {

        if (pType.name() != null && !pType.name().isBlank()) {
            return pType.name();
        }

        String id = pType.id().replace("-", " ").replace("_", " ");
        String[] parts = id.split(" ");
        StringBuilder result = new StringBuilder();

        for (String p : parts) {
            result.append(Character.toUpperCase(p.charAt(0)))
                    .append(p.substring(1))
                    .append(" ");
        }

        return result.toString().trim();
    }
}
