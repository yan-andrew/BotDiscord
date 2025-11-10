package events;

import net.dv8tion.jda.api.events.GenericEvent;

/**
 * Generic event handler contract.
 * Implementations should be stateless or thread-safe.
 */
public interface EventHandler<EventType extends GenericEvent> {
    void handleEvent(EventType event);
    Class<EventType> supports();
}
