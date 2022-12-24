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

package zav.discord.blanc.command;

import java.util.List;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.command.internal.RankValidator;
import zav.discord.blanc.databind.Rank;

/**
 * The command manager is a utility class containing all methods which are required by one or more
 * commands.
 */
@NonNullByDefault
public class CommandManager {
  
  /**
   * The shard the command was executed in.
   */
  protected final Shard shard;
  /**
   * The event from which the active command was created.
   */
  protected final SlashCommandEvent event;
  private final RankValidator validator;
  
  /**
   * Creates a new manager instance. A new instance is created for each command.
   *
   * @param shard The current shard.
   * @param event The event from which the active command was created.
   */
  public CommandManager(Shard shard, SlashCommandEvent event) {
    this.shard = shard;
    this.event = event;
    this.validator = new RankValidator(event.getUser());
  }
  
  /**
   * Checks whether the author of this command has the given rank.
   *
   * @param rank The rank to be validated.
   * @throws InsufficientRankException If the user doesn't have the given rank.
   */
  public void validate(Rank rank) throws InsufficientRankException {
    validator.validate(List.of(rank));
  }
  
  /**
   * Returns the current shard.
   *
   * @return  As described.
   */
  public Shard getShard() {
    return shard;
  }
}
