package event.events;

import net.dv8tion.jda.api.events.GenericEvent;

import java.util.concurrent.ExecutionException;

/**
 * Generic event handler contract.
 * Implementations should be stateless or thread-safe.
 */
public interface EventHandler<EventType extends GenericEvent> {
    void handleEvent(EventType event) throws ExecutionException, InterruptedException;
    Class<EventType> supports();
}
