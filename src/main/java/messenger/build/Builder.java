package messenger.build;

import messenger.messaging.Message;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.Message.Attachment;

import java.awt.Color;
import java.io.File;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;

public interface Builder {
    public abstract void reset();
    public abstract void buildTitle(String pTitle);
    public abstract void buildContent(String pContent);
    public abstract void buildChannel(MessageChannel pChannel);
    public abstract void buildColor(Color pColor);
    public abstract void buildView(ActionRow pView);
    public abstract void buildFile(Attachment pFile) throws ExecutionException, InterruptedException;
    public abstract void buildFile(File pFile);
    public abstract void buildTimestamp(OffsetDateTime pTime);
    public abstract void buildThumbnail(String pUrl);
    public abstract void buildFooter(String pUrl, String pTag);
    public abstract void setType(String pType);
    public abstract Message getResult();
}
