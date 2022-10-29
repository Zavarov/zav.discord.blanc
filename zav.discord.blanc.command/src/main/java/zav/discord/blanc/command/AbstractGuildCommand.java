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

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import net.dv8tion.jda.api.Permission;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.databind.Rank;

/**
 * Base class for all guild commands.<br>
 * Guild commands may require additional Guild-specific permissions in order to be executed.
 */
@NonNullByDefault
public abstract class AbstractGuildCommand extends AbstractCommand {
  
  private final GuildCommandManager manager;
  
  /**
   * Creates a new instance of this class.
   *
   * @param manager The command-specific manager.
   */
  protected AbstractGuildCommand(GuildCommandManager manager) {
    super(Rank.USER, manager);
    this.manager = manager;
  }
  
  @Override
  @Contract(pure = true)
  public void validate() throws ExecutionException {
    super.validate();
    // Has the user the required guild permissions
    manager.validate(getPermissions());
  }
  
  /**
   * Returns the set of permissions required for executing this command. Empty by default, but
   * subclasses may overwrite this method, in order to impose further restrictions.
   * 
   * @return An unmodifiable list of permissions required to execute this command.
   */
  protected Set<Permission> getPermissions() {
    return EnumSet.noneOf(Permission.class);
  }
}
