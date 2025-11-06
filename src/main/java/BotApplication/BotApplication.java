package BotApplication;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import secrets.Config;

import java.io.FileNotFoundException;

public class BotApplication {
    public static void main(String[] args) throws FileNotFoundException {
        String token = Config.getToken();

        if (token == null || token.isBlank()) {
            System.err.println("Not read DISCORD_TOKEN in variables locals");
            System.exit(1);
        }

        JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .build();

        System.out.println("Discord bot is ready");
    }
}