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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.ResourceBundle;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.command.internal.RankValidator;
import zav.discord.blanc.databind.Rank;

/**
 * The command manager is a utility class containing all methods which are required by one or more
 * commands.
 */
@NonNullByDefault
public class CommandManager {
  
  /**
   * The global application instance.
   */
  protected final Client client;
  /**
   * The event from which the active command was created.
   */
  protected final SlashCommandEvent event;
  private final RankValidator validator;
  private final ResourceBundle resourceBundle;
  
  /**
   * Creates a new manager instance. A new instance is created for each command.
   *
   * @param client The global application instance.
   * @param event The event from which the active command was created.
   */
  public CommandManager(Client client, SlashCommandEvent event) {
    this.client = client;
    this.event = event;
    this.validator = new RankValidator(client.getEntityManagerFactory(), event.getUser());
    this.resourceBundle = ResourceBundle.getBundle("i18n");
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
   * Returns the resource bundle containing locale-specific strings.
   *
   * @return As described.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public ResourceBundle getResourceBundle() {
    return resourceBundle;
  }
  
  /**
   * Returns the application instance.
   *
   * @return  As described.
   */
  public Client getClient() {
    return client;
  }
}