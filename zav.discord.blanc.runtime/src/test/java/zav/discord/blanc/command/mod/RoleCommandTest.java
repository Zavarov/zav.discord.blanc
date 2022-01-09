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

package zav.discord.blanc.command.mod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.api.Permission;
import zav.discord.blanc.command.AbstractCommandTest;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.command.InsufficientPermissionException;
import zav.discord.blanc.command.InvalidCommandException;
import zav.discord.blanc.databind.RoleDto;
import zav.discord.blanc.db.RoleDatabase;
import zav.discord.blanc.runtime.command.mod.RoleCommand;

import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class RoleCommandTest extends AbstractCommandTest {
  private Command command;
  
  @BeforeEach
  public void setUp() {
    command = parse("b:mod.role %s %s", roleId, roleGroup);
    
    when(guild.canInteract(eq(member), any())).thenReturn(true);
    when(guild.canInteract(eq(selfMemberView), any())).thenReturn(true);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(RoleCommand.class);
  }
  
  /**
   * Tests whether the role has been made no longer self-assignable.
   */
  @Test
  public void testUngroupRole() throws Exception {
    command.run();
  
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> roleCaptor = ArgumentCaptor.forClass(String.class);
  
    verify(channelView, times(1)).send(msgCaptor.capture(), roleCaptor.capture());
  
    // Correct message?
    assertThat(msgCaptor.getValue()).isEqualTo("Ungrouped role \"%s\".");
    assertThat(roleCaptor.getValue()).isEqualTo(roleName);
  
    // Has the role been updated?
    assertThat(roleDto.getGroup()).isEmpty();
  
    // Has the database been updated?
    RoleDto dbRole = RoleDatabase.get(guildId, roleId);
  
    assertThat(dbRole.getId()).isEqualTo(roleId);
    assertThat(dbRole.getName()).isEqualTo(roleName);
    assertThat(dbRole.getGroup()).isEmpty();
  }
  
  /**
   * Tests whether the role has been made self-assignable.
   */
  @Test
  public void testGroupRole() throws Exception {
    roleDto.setGroup(null);
    
    command.run();
  
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> roleCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> groupCaptor = ArgumentCaptor.forClass(String.class);
  
    verify(channelView, times(1)).send(msgCaptor.capture(), roleCaptor.capture(), groupCaptor.capture());
  
    // Correct message?
    assertThat(msgCaptor.getValue()).isEqualTo("The role \"%s\" has been grouped under \"%s\".");
    assertThat(roleCaptor.getValue()).isEqualTo(roleName);
    assertThat(groupCaptor.getValue()).isEqualTo(roleGroup);
  
    // Has the role been updated?
    assertThat(roleDto.getGroup()).contains(roleGroup);
  
    // Has the database been updated?
    RoleDto dbRole = RoleDatabase.get(guildId, roleId);
  
    assertThat(dbRole.getId()).isEqualTo(roleId);
    assertThat(dbRole.getName()).isEqualTo(roleName);
    assertThat(dbRole.getGroup()).contains(roleGroup);
  }
  
  /**
   * Tests whether the role couldn't be made self-assignable for this group, because it is already
   * self-assignable for a different group.
   */
  @Test
  public void testRoleInDifferentGroup() throws Exception {
    roleDto.setGroup("foo");
  
    command.run();
  
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> roleCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> groupCaptor = ArgumentCaptor.forClass(String.class);
  
    verify(channelView, times(1)).send(msgCaptor.capture(), roleCaptor.capture(), groupCaptor.capture());
  
    // Correct message?
    assertThat(msgCaptor.getValue()).isEqualTo("The role \"%s\" is already grouped under \"%s\".");
    assertThat(roleCaptor.getValue()).isEqualTo(roleName);
    assertThat(groupCaptor.getValue()).isEqualTo("foo");
  
    // The role group shouldn't have been modified
    assertThat(roleDto.getGroup()).contains("foo");
  
    // The database shouldn't have been updated
    assertThrows(NoSuchElementException.class, () -> RoleDatabase.get(guildId, roleId));
  }
  
  /**
   * Tests whether the role couldn't be made self-assignable because the author lacks permission
   * to assign it manually.
   */
  @Test
  public void testUserCantInteract() throws Exception {
    // Change author from (selfUser) to (user)
    when(messageView.getAuthor()).thenReturn(member);
    when(guild.canInteract(eq(member), any())).thenReturn(false);
  
    command.run();
  
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> roleCaptor = ArgumentCaptor.forClass(String.class);
  
    verify(channelView, times(1)).send(msgCaptor.capture(), roleCaptor.capture());
  
    // Correct message?
    assertThat(msgCaptor.getValue()).isEqualTo("You need to be able to interact with the role \"%s\".");
    assertThat(roleCaptor.getValue()).isEqualTo(roleName);
  
    // The role group shouldn't have been modified
    assertThat(roleDto.getGroup()).contains(roleGroup);
  
    // The database shouldn't have been updated
    assertThrows(NoSuchElementException.class, () -> RoleDatabase.get(guildId, roleId));
  
  }
  
  /**
   * Tests whether the role couldn't be made self-assignable because the bot lacks permission
   * to assign it.
   */
  @Test
  public void testBotCantInteract() throws Exception {
    // Change author from (selfUser) to (user)
    when(messageView.getAuthor()).thenReturn(member);
    when(guild.canInteract(eq(selfMemberView), any())).thenReturn(false);
  
    command.run();
  
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> roleCaptor = ArgumentCaptor.forClass(String.class);
  
    verify(channelView, times(1)).send(msgCaptor.capture(), roleCaptor.capture());
  
    // Correct message?
    assertThat(msgCaptor.getValue()).isEqualTo("I need to be able to interact with the role \"%s\".");
    assertThat(roleCaptor.getValue()).isEqualTo(roleName);
  
    // The role group shouldn't have been modified
    assertThat(roleDto.getGroup()).contains(roleGroup);
  
    // The database shouldn't have been updated
    assertThrows(NoSuchElementException.class, () -> RoleDatabase.get(guildId, roleId));
  }
  
  
  @Test
  public void testCheckPermissions() throws InvalidCommandException {
    when(member.getPermissions()).thenReturn(Set.of(Permission.MANAGE_ROLES));
    
    // No error
    command.validate();
  }
  
  @Test
  public void testCheckMissingPermission() {
    assertThrows(InsufficientPermissionException.class, () -> command.validate());
  }
}