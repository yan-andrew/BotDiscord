package command.meta;

import command.core.CommandMetadata;

import java.util.List;

public interface CommandMetadataProvider {
    List<CommandMetadata> load() throws Exception;
}