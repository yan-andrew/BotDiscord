package command.core;

public record OptionMetadata(
        String name,
        String description,
        String type,
        boolean required
) { }
