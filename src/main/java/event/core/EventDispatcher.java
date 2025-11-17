// File: events/core/EventDispatcher.java
package event.core;

import java.util.*;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.GenericEvent;
import event.events.EventHandler;

/**
 * Centralizes delivery of JDA events to registered handlers.
 */
public final class EventDispatcher extends ListenerAdapter {
    private final List<EventHandler<? extends GenericEvent>> handlers = new ArrayList<>();


    public EventDispatcher register(EventHandler<? extends GenericEvent> handler) {
        handlers.add(handler);
        return this;
    }
    
    @Override
    public void onGenericEvent(GenericEvent event) {
        // Simple linear dispatch by exact supported type
        for (EventHandler<? extends GenericEvent> h : handlers) {
            if (h.supports().isInstance(event)) {
                dispatch(h, event);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void dispatch(EventHandler handler, GenericEvent event) {
        handler.handleEvent(event);
    }
}
