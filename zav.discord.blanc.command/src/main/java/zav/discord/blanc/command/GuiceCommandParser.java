package zav.discord.blanc.command;

import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.CommandParser;
import zav.discord.blanc.command.internal.CommandModule;
import zav.discord.blanc.command.internal.GuildCommandModule;

/**
 * An implementation of the command parser using Guice. Dependency injection is used to inject the
 * constructor arguments.
 */
@Singleton
public class GuiceCommandParser implements CommandParser {
  private static final Logger LOGGER = LoggerFactory.getLogger(GuiceCommandParser.class);
  private final Client client;
  private final Injector injector;
  
  /**
   * Creates a new instance of this class.
   *
   * @param client The global application instance.
   * @param injector The Guice injector used to create the commands.
   */
  @Inject
  public GuiceCommandParser(Client client, Injector injector) {
    this.client = client;
    this.injector = injector;
  }
  
  @Override
  public Optional<Command> parse(SlashCommandEvent event) {
    
    Class<? extends Command> clazz = Commands.get(getQualifiedName(event)).orElse(null);
    
    if (clazz == null) {
      LOGGER.error("Unknown slash command {}.", event.getName());
      return Optional.empty();
    }
  
    Module module;
    
    if (event.isFromGuild()) {
      module = new GuildCommandModule(client, event);
    } else {
      module = new CommandModule(client, event);
    }

    Command cmd = injector.createChildInjector(module).getInstance(clazz);
    
    return Optional.of(cmd);
  }
  
  @Contract(pure = true)
  private String getQualifiedName(SlashCommandEvent event) {
    List<String> parts = new ArrayList<>(3);
    
    parts.add(event.getName());
  
    Optional.ofNullable(event.getSubcommandGroup()).ifPresent(parts::add);
    Optional.ofNullable(event.getSubcommandName()).ifPresent(parts::add);
    return String.join(".", parts);
  }

}
