package messaging;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

public final class Attached extends Message{
    private final File file;

    public Attached(String pTitle, String pContent, MessageChannel pChannel, File pFile) {
        super (pTitle, pContent, pChannel);
        file = pFile;
    }

    @Override
    public void sendMessage() {
        String text = buildText();

        channel.sendMessage(text).addFiles(FileUpload.fromData(file)).queue();
    }
}
