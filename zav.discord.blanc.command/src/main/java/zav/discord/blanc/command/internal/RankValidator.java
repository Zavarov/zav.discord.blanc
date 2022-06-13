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

package zav.discord.blanc.command.internal;

import static zav.discord.blanc.api.Rank.getEffectiveRanks;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.User;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.InsufficientRankException;
import zav.discord.blanc.db.UserTable;

/**
 * This class checks whether the user executing the command has the required rank for execution.
 * An {@link InsufficientRankException} is thrown, if not.
 */
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "That's the point...")
public class RankValidator implements Validator<Rank> {
  private final UserTable db;
  private final User author;
  
  /**
   * Initializes the rank validator for a single command.
   *
   * @param db The database containing all registered user ranks.
   * @param author The user who executed the command.
   */
  @Inject
  public RankValidator(UserTable db, User author) {
    this.db = db;
    this.author = author;
  }
  
  @Override
  public void validate(Collection<Rank> args) throws InsufficientRankException {
    if (!getEffectiveRanks(db, author).containsAll(args)) {
      throw new InsufficientRankException(args);
    }
  }
}
