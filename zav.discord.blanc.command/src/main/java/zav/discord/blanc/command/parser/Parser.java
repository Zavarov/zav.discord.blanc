package zav.discord.blanc.command.parser;

import java.util.Optional;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.command.Commands;
import zav.discord.blanc.databind.MessageValueObject;
import zav.discord.blanc.view.GuildMessageView;
import zav.discord.blanc.view.PrivateMessageView;

/**
 * Base interface for all command parser.<br>
 * The parsing process itself consists of two steps. First the raw string is transformed into
 * an intermediate representation, where name, arguments and all other relevant parameter are
 * extracted. Then this representation is used to instantiate the actual command.<br>
 * The classes corresponding to the commands are taken from {@link Command}.
 *
 * @see Commands
 */
public interface Parser {
  @Nullable IntermediateCommand parse(MessageValueObject content);
  
  Optional<? extends Command> parse(GuildMessageView source);
  
  Optional<? extends Command> parse(PrivateMessageView source);
}
