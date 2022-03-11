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

import java.util.Set;
import javax.inject.Inject;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.db.UserTable;

/**
 * Base class for all guild commands.<br>
 * Guild commands may require additional Guild-specific permissions in order to be executed.
 */
@NonNullByDefault
public abstract class AbstractGuildCommand extends AbstractCommand {
  @Inject
  protected Guild guild;
  @Inject
  protected TextChannel channel;
  @Inject
  protected Member author;
  @Inject
  protected Message message;
  @Inject
  private UserTable db;
  
  private final Set<Permission> permissions;
  
  protected AbstractGuildCommand(Permission... permissions) {
    this(Rank.USER, permissions);
  }
  
  protected AbstractGuildCommand(Rank rank) {
    this(rank, new Permission[0]);
  }
  
  protected AbstractGuildCommand(Rank rank, Permission... permissions) {
    super(rank);
    this.permissions = Set.of(permissions);
  }
  
  @Override
  @Contract(pure = true)
  public void validate() throws Exception {
    super.validate();
    
    boolean isRoot = Rank.getEffectiveRanks(db, author.getUser()).contains(Rank.ROOT);
    boolean hasRank = author.getPermissions(channel).containsAll(permissions);
    
    // Does the user have the required permissions?
    if (!isRoot && ! hasRank) {
      throw new InsufficientPermissionException();
    }
  }
}
