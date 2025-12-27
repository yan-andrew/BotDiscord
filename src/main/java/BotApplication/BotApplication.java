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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import access.secrets.Config;
import access.creational.ConexionDBSingleton;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class BotApplication {
    private static Map<Long, Message> messageCache = new ConcurrentHashMap<>();

    public static void addMessage(Message pMessage) {
        messageCache.put(pMessage.getIdLong(), pMessage);
    }

    public static Message removeMessage(Long pId) {
        return messageCache.remove(pId);
    }

    public static void main(String[] args) throws Exception {
        var cx = ConexionDBSingleton.getInstance();
        cx.connect();

        var dispatcher = new EventDispatcher()
                .register(new GuildJoinEventHandler())
                .register(new MessageReceivedEventHandler())
                .register(new GuildBanEventHandler())
                .register(new VoiceChannelJoinEvent())
                .register(new GuildUnBanEventHandler())
                .register(new GuildMemberUpdateNickname())
                .register(new GuildMemberUpdateTimeout())
                .register(new GuildMemberRoleAdd())
                .register(new GuildMemberRoleRemove())
                .register(new MessageDeleteEventHandler())
                .register(new MessageUpdateEventHandler())
                .register(new ChannelCreateEventHandler())
                .register(new ChannelDeleteEventHandler())
                .register(new InviteCreateEventHandler())
                .register(new InviteDeleteEventHandler())
                .register(new GuildMemberJoinEventHandler())
                .register(new GuildMemberRemoveEventHandler());

        String token = Config.getToken();

        if (token == null || token.isBlank()) {
            System.err.println("Not read DISCORD_TOKEN in variables locals");
            System.exit(1);
        }

        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MODERATION,
                        GatewayIntent.GUILD_INVITES,
                        GatewayIntent.GUILD_VOICE_STATES
                )
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(dispatcher)
                .build();

        CommandMetadataProvider metaProvider =
                new JsonCommandMetadataProvider(Paths.get("commands.json"));
        List<CommandMetadata> metas = metaProvider.load();

        CommandMetadataIndex metaIndex = new CommandMetadataIndex();
        metaIndex.load(metas);

        CommandRegistry registry = new CommandRegistry();
        registry.register(new RegisterChannelCommand());
        registry.register(new AutoResponseCommand());
        registry.register(new MessageCommand());
        registry.register(new AdvertisementCommand());

        SlashPublisher publisher = new SlashPublisher();
        publisher.publishAll(jda, metaIndex);

        jda.addEventListener(new CommandRouter(registry, metaIndex));

        System.out.println("Discord bot is ready");
    }
}