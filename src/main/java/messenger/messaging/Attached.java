package messenger.messaging;

import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.entities.Message.Attachment;

import java.io.File;

public final class Attached extends Message {
    private FileUpload  file;

    public Attached() {

    }

    public void setFile(FileUpload  pFile) {
        file = pFile;
    }

    @Override
    public void sendMessage() {
        String text = buildText();

        if (file == null) {
            channel.sendMessage(text).queue();
            return;
        }

        channel.sendMessage(text)
                .addFiles(file)
                .queue();
    }
}
