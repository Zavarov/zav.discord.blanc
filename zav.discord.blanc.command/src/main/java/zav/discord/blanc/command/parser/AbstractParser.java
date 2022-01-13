package zav.discord.blanc.command.parser;

import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.Optional;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Message;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.command.Command;
import zav.discord.blanc.api.command.Commands;
import zav.discord.blanc.api.command.IntermediateCommand;
import zav.discord.blanc.api.command.parser.Parser;
import zav.discord.blanc.command.internal.IntermediateCommandModule;

/**
 * Abstract base class for all command parser that implement the conversion from the intermediate
 * command representation to a Java object.
 */
public abstract class AbstractParser implements Parser {
  @Inject
  private Injector injector;
  
  @Override
  public Optional<Command> parse(Module module, Message message) {
    @Nullable
    IntermediateCommand cmd = parse(message);
    
    // Input is not a valid command
    if (cmd == null) {
      return Optional.empty();
    }
    
    Module cmdModule = new IntermediateCommandModule(cmd);
    
    return Commands.get(cmd.getName()).map(cmdClass -> {
      // Injector w/ JDA & arguments
      Injector cmdInjector = injector.createChildInjector(module, cmdModule);
      
      return cmdInjector.getInstance(cmdClass);
    });
  }
}
