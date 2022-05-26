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

import java.util.Collection;
import java.util.Objects;
import javax.inject.Inject;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.Nullable;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.InsufficientPermissionException;
import zav.discord.blanc.db.UserTable;

/**
 * This class checks whether the user executing the command has the required permissions for
 * execution. An {@link InsufficientPermissionException} is thrown, if not.
 */
public class PermissionValidator implements Validator<Permission> {
  private @Nullable UserTable db;
  private @Nullable Member author;
  private @Nullable TextChannel textChannel;
  
  @Inject
  /*package*/ void setDatabase(UserTable db) {
    this.db = db;
  }
  
  @Inject
  /*package*/ void setAuthor(Member author) {
    this.author = author;
  }
  
  @Inject
  /*package*/ void setTextChannel(TextChannel textChannel) {
    this.textChannel = textChannel;
  }
  
  @Override
  public void validate(Collection<Permission> args) throws InsufficientPermissionException {
    Objects.requireNonNull(db);
    Objects.requireNonNull(author);
    Objects.requireNonNull(textChannel);
    
    boolean isRoot = Rank.getEffectiveRanks(db, author.getUser()).contains(Rank.ROOT);
    boolean hasRank = author.getPermissions(textChannel).containsAll(args);
  
    // Does the user have the required permissions?
    if (!isRoot && !hasRank) {
      throw new InsufficientPermissionException();
    }
  }
}
