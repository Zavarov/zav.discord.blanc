package zav.discord.blanc.runtime.internal;

import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.CommandParser;
import zav.discord.blanc.api.CommandProvider;
import zav.discord.blanc.api.Shard;

/**
 * An implementation of the command parser using Guice. Dependency injection is used to inject the
 * constructor arguments.
 */
public class SimpleCommandParser implements CommandParser {
  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCommandParser.class);
  private final Shard shard;
  private final CommandProvider provider;
  
  /**
   * Creates a new instance of this class.
   *
   * @param shard The current shard.
   */
  public SimpleCommandParser(Shard shard, CommandProvider provider) {
    this.shard = shard;
    this.provider = provider;
  }
  
  @Override
  public Optional<Command> parse(SlashCommandEvent event) {
    Optional<Command> command = provider.create(shard, event);
    
    if (command.isEmpty()) {
      LOGGER.error("Unknown slash command {}.", event.getName());
      return Optional.empty();
    }
    
    return command;
  }
}
