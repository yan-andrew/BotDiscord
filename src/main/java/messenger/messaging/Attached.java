package messenger.messaging;

import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

public final class Attached extends Message{
    private File file;

    public Attached() {

    }

    public void setFile(File pFile) {
        file = pFile;
    }

    @Override
    public void sendMessage() {
        String text = buildText();

        channel.sendMessage(text).addFiles(FileUpload.fromData(file)).queue();
    }
}
