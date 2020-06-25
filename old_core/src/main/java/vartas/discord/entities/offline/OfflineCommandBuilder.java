package vartas.discord.entities.offline;

import net.dv8tion.jda.api.entities.Message;
import vartas.discord.Command;
import vartas.discord.CommandBuilder;

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
