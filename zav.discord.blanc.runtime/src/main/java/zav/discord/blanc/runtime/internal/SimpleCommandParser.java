/*
 * Copyright (c) 2022 Zavarov.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
   * @param provider The provider for all registered commands.
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
