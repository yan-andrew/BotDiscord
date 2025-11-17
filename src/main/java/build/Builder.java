package build;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.awt.Color;

public interface Builder {
    public abstract void reset();
    public abstract void buildTitle(String pTitle);
    public abstract void buildContent(String pContent);
    public abstract void buildChannel(MessageChannel pChannel);
    public abstract void buildColor(Color pColor);
    public abstract void buildView(String pView);
    public abstract void buildFile(String pFile);
}
