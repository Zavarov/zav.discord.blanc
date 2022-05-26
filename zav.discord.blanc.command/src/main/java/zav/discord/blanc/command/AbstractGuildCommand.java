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

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.internal.PermissionValidator;

/**
 * Base class for all guild commands.<br>
 * Guild commands may require additional Guild-specific permissions in order to be executed.
 */
public abstract class AbstractGuildCommand extends AbstractCommand {
  
  private @Nullable PermissionValidator validator;
  
  private final Set<Permission> permissions;
  
  protected AbstractGuildCommand(Permission... permissions) {
    this(Rank.USER, permissions);
  }
  
  protected AbstractGuildCommand(Rank rank, Permission... permissions) {
    super(rank);
    this.permissions = Set.of(permissions);
  }
  
  @Inject
  /*package*/ void setValidator(PermissionValidator validator) {
    this.validator = validator;
  }
  
  @Override
  @Contract(pure = true)
  public void validate() throws ExecutionException {
    Objects.requireNonNull(validator);
    super.validate();
    // Has the user the required guild permissions
    validator.validate(permissions);
  }
}
