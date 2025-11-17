package command.meta;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import command.core.CommandMetadata;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

// comments in English
public class JsonCommandMetadataProvider implements CommandMetadataProvider {
    private final Path path;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonCommandMetadataProvider(Path pPath) {
        path = pPath;
    }

    @Override
    public List<CommandMetadata> load() throws Exception {
        return mapper.readValue(new File(path.toString()), new TypeReference<>() {});
    }
}