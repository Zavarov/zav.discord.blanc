package zav.discord.blanc.command.parser;

import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.Optional;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Message;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.command.Command;
import zav.discord.blanc.api.command.Commands;
import zav.discord.blanc.api.command.IntermediateCommand;
import zav.discord.blanc.api.command.parser.Parser;
import zav.discord.blanc.command.internal.IntermediateCommandModule;
import zav.discord.blanc.command.internal.ParameterModule;

/**
 * Abstract base class for all command parser that implement the conversion from the intermediate
 * command representation to a Java object.
 */
@NonNullByDefault
public abstract class AbstractParser implements Parser {
  @Inject
  private Injector injector;
  
  @Override
  @Contract(pure = true)
  public Optional<Command> parse(Module module, Message message) {
    @Nullable
    IntermediateCommand cmd = parse(message);
    
    // Input is not a valid command
    if (cmd == null) {
      return Optional.empty();
    }
    
    Module cmdModule = new IntermediateCommandModule(cmd);
    Module paramModule = new ParameterModule(message, cmd.getParameters());
    
    return Commands.get(cmd.getName()).map(cmdClass -> {
      // Injector w/ JDA & arguments
      Injector cmdInjector = injector.createChildInjector(module, cmdModule, paramModule);
      
      return cmdInjector.getInstance(cmdClass);
    });
  }
}
