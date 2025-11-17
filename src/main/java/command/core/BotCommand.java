package command.core;

public interface BotCommand {
    String id();
    void execute(CommandContext ctx) throws Exception;
}
