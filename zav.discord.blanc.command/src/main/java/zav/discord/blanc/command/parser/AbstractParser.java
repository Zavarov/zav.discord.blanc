package zav.discord.blanc.command.parser;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.Optional;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.GuildMessage;
import zav.discord.blanc.api.Message;
import zav.discord.blanc.api.PrivateMessage;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.command.Commands;
import zav.discord.blanc.command.guice.GuildCommandModule;
import zav.discord.blanc.command.guice.PrivateCommandModule;
import zav.discord.blanc.command.internal.IntermediateCommandModule;

/**
 * Abstract base class for all command parser that implement the conversion from the intermediate
 * command representation to a Java object.
 */
public abstract class AbstractParser implements Parser {
  @Override
  public Optional<? extends Command> parse(GuildMessage msg) {
    return parse(new GuildCommandModule(msg), msg);
  }
  
  @Override
  public Optional<? extends Command> parse(PrivateMessage msg) {
    return parse(new PrivateCommandModule(msg), msg);
  }
  
  private Optional<? extends Command> parse(AbstractModule msgModule, Message msg) {
    @Nullable
    IntermediateCommand cmd = parse(msg.getAbout());
    
    // Input is not a valid command
    if (cmd == null) {
      return Optional.empty();
    }
    
    IntermediateCommandModule cmdModule = new IntermediateCommandModule(cmd);
    
    return Commands.get(cmd.getName()).map(cmdClass -> {
      Injector injector = Guice.createInjector(msgModule, cmdModule);
      
      Command result = injector.getInstance(cmdClass);
      
      result.postConstruct(cmd.getArguments());
      
      return result;
    });
  }
}
