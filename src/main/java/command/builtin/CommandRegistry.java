package command.builtin;

import command.core.BotCommand;

import java.util.*;

public class CommandRegistry {
    private final Map<String, BotCommand> byId = new HashMap<>();

    public void register(BotCommand cmd) {
        byId.put(cmd.id(), cmd);
    }

    public Optional<BotCommand> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public Collection<BotCommand> all() {
        return Collections.unmodifiableCollection(byId.values());
    }
}
