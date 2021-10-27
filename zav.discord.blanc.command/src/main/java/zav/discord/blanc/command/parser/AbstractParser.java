package zav.discord.blanc.command.parser;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.command.Commands;
import zav.discord.blanc.command.internal.GuildCommandModule;
import zav.discord.blanc.command.internal.IntermediateCommandModule;
import zav.discord.blanc.command.internal.PrivateCommandModule;
import zav.discord.blanc.view.GuildMessageView;
import zav.discord.blanc.view.MessageView;
import zav.discord.blanc.view.PrivateMessageView;

import java.util.Optional;

public abstract class AbstractParser implements Parser {
  @Override
  public Optional<? extends Command> parse(GuildMessageView msg) {
    return parse(new GuildCommandModule(msg), msg);
  }
  
  @Override
  public Optional<? extends Command> parse(PrivateMessageView msg) {
    return parse(new PrivateCommandModule(msg), msg);
  }
  
  private Optional<? extends Command> parse(AbstractModule msgModule, MessageView msg) {
    IntermediateCommand cmd = parse(msg.getAbout());
    
    // Input is not a valid command
    if (cmd == null) {
      return Optional.empty();
    }
    
    IntermediateCommandModule cmdModule = new IntermediateCommandModule(cmd);
    Injector injector = Guice.createInjector(msgModule, cmdModule);
    
    Optional<Command> result = Commands.get(cmd.getName(), cmd.getArguments());
    
    result.ifPresent(injector::injectMembers);
    
    return result;
  }
}
