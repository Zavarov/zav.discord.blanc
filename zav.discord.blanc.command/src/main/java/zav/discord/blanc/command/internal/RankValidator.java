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

import java.util.Collection;
import java.util.Objects;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.InsufficientRankException;
import zav.discord.blanc.db.UserTable;

/**
 * This class checks whether the user executing the command has the required rank for execution.
 * An {@link InsufficientRankException} is thrown, if not.
 */
public class RankValidator implements Validator<Rank> {
  private @Nullable UserTable db;
  private @Nullable User author;
  
  @Inject
  /*package*/ void setDatabase(UserTable db) {
    this.db = db;
  }
  
  @Inject
  /*package*/ void setAuthor(User author) {
    this.author = author;
  }
  
  @Override
  public void validate(Collection<Rank> args) throws InsufficientRankException {
    Objects.requireNonNull(db);
    Objects.requireNonNull(author);
    
    if (!getEffectiveRanks(db, author).containsAll(args)) {
      throw new InsufficientRankException(args);
    }
  }
}
