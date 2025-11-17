package BotApplication;

import command.builtin.*;
import command.commands.*;
import command.core.CommandMetadata;
import command.core.CommandMetadataIndex;
import command.meta.*;
import event.events.*;
import event.core.EventDispatcher;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.nio.file.Paths;
import java.util.List;

import access.secrets.Config;
import access.creational.ConexionDBSingleton;

public class BotApplication {
    public static void main(String[] args) throws Exception {
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

        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(dispatcher)
                .build();

        CommandMetadataProvider metaProvider =
                new JsonCommandMetadataProvider(Paths.get("commands.json"));
        List<CommandMetadata> metas = metaProvider.load();

        CommandMetadataIndex metaIndex = new CommandMetadataIndex();
        metaIndex.load(metas);

        CommandRegistry registry = new CommandRegistry();
        registry.register(new RegisterChannelCommand());

        SlashPublisher publisher = new SlashPublisher();
        publisher.publishAll(jda, metaIndex);

        jda.addEventListener(new CommandRouter(registry, metaIndex));

        System.out.println("Discord bot is ready");
    }
}