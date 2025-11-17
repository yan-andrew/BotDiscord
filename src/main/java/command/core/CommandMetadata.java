package command.core;

import java.util.List;

public record CommandMetadata(
        String id,
        String name,
        String description,
        List<String> aliases,
        boolean enabled
) { }