package vartas.discord.bot.entities.offline;

import net.dv8tion.jda.api.entities.Message;
import vartas.discord.bot.Command;
import vartas.discord.bot.CommandBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class OfflineCommandBuilder extends CommandBuilder {
    public final Map<Message, Command> commands = new HashMap<>();

    @Override
    public Command build(String content, Message source) {
        if(commands.containsKey(source))
            return commands.get(source);
        else
            throw new NoSuchElementException();
    }
}
