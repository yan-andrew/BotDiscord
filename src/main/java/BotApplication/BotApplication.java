package BotApplication;

import events.GuildJoinEventHandler;
import events.MessageReceivedEventHandler;
import eventscore.EventDispatcher;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.io.FileNotFoundException;

import secrets.Config;
import creational.ConexionDBSingleton;

public class BotApplication {
    public static void main(String[] args) throws FileNotFoundException {
        var cx = ConexionDBSingleton.getInstance();
        cx.connect();

        var dispatcher = new EventDispatcher()
                .register(new GuildJoinEventHandler())
                .register(new MessageReceivedEventHandler());

        String token = Config.getToken();

        if (token == null || token.isBlank()) {
            System.err.println("Not read DISCORD_TOKEN in variables locals");
            System.exit(1);
        }

        JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(dispatcher)
                .build();

        System.out.println("Discord bot is ready");
    }
}