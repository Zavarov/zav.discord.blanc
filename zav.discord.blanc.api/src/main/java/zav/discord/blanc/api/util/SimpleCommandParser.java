package zav.discord.blanc.api.util;

import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.CommandParser;
import zav.discord.blanc.api.CommandProvider;

/**
 * An implementation of the command parser using Guice. Dependency injection is used to inject the
 * constructor arguments.
 */
public class SimpleCommandParser implements CommandParser {
  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCommandParser.class);
  private final Client client;
  private final CommandProvider provider;
  
  /**
   * Creates a new instance of this class.
   *
   * @param client The global application instance.
   */
  public SimpleCommandParser(Client client, CommandProvider provider) {
    this.client = client;
    this.provider = provider;
  }
  
  @Override
  public Optional<Command> parse(SlashCommandEvent event) {
    Optional<Command> command = provider.create(client, event);
    
    if (command.isEmpty()) {
      LOGGER.error("Unknown slash command {}.", event.getName());
      return Optional.empty();
    }
    
    return command;
  }
}
