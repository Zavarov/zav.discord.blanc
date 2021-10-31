/*
 * Copyright (c) 2020 Zavarov
 *
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

package zav.discord.blanc.runtime.command.guild;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.Validate;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.Argument;
import zav.discord.blanc.databind.Role;
import zav.discord.blanc.view.RoleView;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class AssignCommand extends AbstractGuildCommand {
  private static final LoadingCache<Long, Semaphore> activeMembers = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(5))
        .build(CacheLoader.from(() -> new Semaphore(1)));
  private Role myRole;
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    Validate.validIndex(args, 0);
    myRole = guild.getRole(args.get(0)).getAbout();
  }

  /**
   * First, the roles are received. If the role is self-assignable, it is added
   * to the user who requested it, if the person doesn't already have the role.
   * Otherwise it is removed.
   */
  @Override
  public void run() throws Exception {
    Semaphore lock = activeMembers.get(author.getAbout().getId());
    
    // Command took too long to acquire lock => fail
    if (!lock.tryAcquire(1, TimeUnit.MINUTES)) {
      throw new TimeoutException();
    }
    
    // Enter critical section
    try {
      if (isGrouped()) {
        if (isAssigned()) {
          // Remove role
          author.removeRole(myRole);
          channel.send("You no longer have the role \"%s\" from group \"%s\".", myRole.getName(), myRole.getGroup());
        } else {
          // Assign role
          Collection<Role> rolesToRemove = getConflictingRoles();
          Collection<Role> rolesToAdd = Collections.singleton(myRole);
          author.modifyRoles(rolesToAdd, rolesToRemove);
          channel.send("You now have the role \"%s\" from group \"%s\".", myRole.getName(), myRole.getGroup());
        }
      } else {
        // Invalid role
        channel.send("The role \"%s\" isn't self-assignable.", myRole.getName());
      }
    } finally {
      // Allow new command from the same user to be processed
      lock.release();
    }
  }
  
  private Collection<Role> getConflictingRoles() {
    return author.getRoles().stream()
          .map(RoleView::getAbout)
          .filter(role -> Objects.equals(role.getGroup(), myRole.getGroup()))
          .collect(Collectors.toUnmodifiableList());
  }
  
  private boolean isAssigned() {
    return author.getRoles().stream()
          .map(RoleView::getAbout)
          .anyMatch(role -> Objects.equals(role.getId(), myRole.getId()));
  }
  
  private boolean isGrouped() {
    return myRole.getGroup() != null;
  }
}
