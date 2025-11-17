package command.core;

import java.util.*;

public class CommandMetadataIndex {
    private final Map<String, CommandMetadata> byId = new HashMap<>();
    private final Map<String, String> nameToId = new HashMap<>();

    public void load(Collection<CommandMetadata> metas) {
        byId.clear();
        nameToId.clear();
        for (var m : metas) {
            if (m == null || m.id() == null || m.name() == null) continue;
            byId.put(m.id(), m);
            nameToId.put(m.name().toLowerCase(), m.id());
            if (m.aliases() != null) {
                for (var a : m.aliases()) {
                    if (a != null && !a.isBlank()) {
                        nameToId.put(a.toLowerCase(), m.id());
                    }
                }
            }
        }
    }

    public Optional<CommandMetadata> byId(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public Optional<String> idBySlashName(String slashName) {
        if (slashName == null) return Optional.empty();
        return Optional.ofNullable(nameToId.get(slashName.toLowerCase()));
    }

    public Collection<CommandMetadata> all() {
        return Collections.unmodifiableCollection(byId.values());
    }
}