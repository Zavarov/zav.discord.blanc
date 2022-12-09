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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.command.InsufficientPermissionException;
import zav.discord.blanc.databind.Rank;

/**
 * This class checks whether the user executing the command has the required permissions for
 * execution. An {@link InsufficientPermissionException} is thrown, if not.
 */
@NonNullByDefault
public class PermissionValidator implements Validator<Permission> {
  private final Member author;
  private final TextChannel textChannel;
  
  /**
   * Initializes the permission validator for a single command.
   *
   * @param author The user who executed the command.
   * @param textChannel The channel the command was executed in.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public PermissionValidator(Member author, TextChannel textChannel) {
    this.author = author;
    this.textChannel = textChannel;
  }
  
  @Override
  public void validate(Collection<Permission> args) throws InsufficientPermissionException {
    boolean isRoot = Rank.getEffectiveRanks(author.getUser()).contains(Rank.ROOT);
    boolean hasPermission = author.getPermissions(textChannel).containsAll(args);
  
    // Does the user have the required permissions?
    if (!isRoot && !hasPermission) {
      throw new InsufficientPermissionException(args);
    }
  }
}
