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

package zav.discord.blanc.command.core;

import com.google.common.cache.LoadingCache;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import zav.discord.blanc.command.AbstractCommandTest;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.databind.RoleDto;
import zav.discord.blanc.runtime.command.core.AssignCommand;
import zav.discord.blanc.api.Role;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

public class AssignCommandTest extends AbstractCommandTest {
  private static final String ACTIVE_MEMBERS = "activeMembers";
  private Command command;
  private RoleDto roleDtoMock;
  private Role roleMock;
  
  @Captor
  private ArgumentCaptor<Collection<RoleDto>> addedRoles;
  
  @Captor
  private ArgumentCaptor<Collection<RoleDto>> removedRoles;
  
  @Captor
  private ArgumentCaptor<RoleDto> removedRole;
  
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    
    command = parse("b:assign %s", roleId);
    
    // User may already have a role in the same group
    roleDtoMock = mock(RoleDto.class);
    roleMock = mock(Role.class);
    when(roleDtoMock.getGroup()).thenReturn(Optional.of(roleGroup));
    when(roleMock.getAbout()).thenReturn(roleDtoMock);
  }
  
  @AfterEach
  public void cleanUp() {
    LoadingCache<Long, Semaphore> activeMembers = Whitebox.getInternalState(AssignCommand.class, ACTIVE_MEMBERS);
    activeMembers.invalidateAll();
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(AssignCommand.class);
  }
  
  /**
   * Checks whether the role could be assigned to the author.
   */
  @Test
  public void testAssignRole() throws Exception {
    // User already has a different role in the same group => replace
    doReturn(Set.of(roleMock)).when(member).getRoles();
    
    command.run();
    
    ArgumentCaptor<String> msgCaptorCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> roleCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> groupCaptor = ArgumentCaptor.forClass(String.class);
    
    verify(channelView, times(1)).send(msgCaptorCaptor.capture(), roleCaptor.capture(), groupCaptor.capture());
    
    // Correct message?
    assertThat(msgCaptorCaptor.getValue()).isEqualTo("You now have the role \"%s\" from group \"%s\".");
    assertThat(roleCaptor.getValue()).isEqualTo(roleName);
    assertThat(groupCaptor.getValue()).isEqualTo(roleGroup);
    
    // Has the role been replaced?
    verify(member, times(1)).modifyRoles(addedRoles.capture(), removedRoles.capture());
    
    assertThat(addedRoles.getValue()).containsExactly(roleDto);
    assertThat(removedRoles.getValue()).containsExactly(roleDtoMock);
  }
  
  /**
   * Checks whether the role could be removed from the author.
   */
  @Test
  public void testRemoveRole() throws Exception {
    // User has the given role assigned => remove
    doReturn(Set.of(role)).when(member).getRoles();
  
    command.run();
  
    ArgumentCaptor<String> msgCaptorCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> roleCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> groupCaptor = ArgumentCaptor.forClass(String.class);
  
    verify(channelView, times(1)).send(msgCaptorCaptor.capture(), roleCaptor.capture(), groupCaptor.capture());
  
    // Correct message?
    assertThat(msgCaptorCaptor.getValue()).isEqualTo("You no longer have the role \"%s\" from group \"%s\".");
    assertThat(roleCaptor.getValue()).isEqualTo(roleName);
    assertThat(groupCaptor.getValue()).isEqualTo(roleGroup);
  
    // Has the role been removed?
    verify(member, times(1)).removeRole(removedRole.capture());
  
    assertThat(removedRole.getValue()).isEqualTo(roleDto);
  }
  
  /**
   * Checks whether the role couldn't be modified, because it is un-assignable.
   */
  @Test
  public void testInvalidRole() throws Exception {
    // Role is not assignable
    roleDto.setGroup(null);
    doReturn(Set.of(role)).when(member).getRoles();
  
    command.run();
  
    ArgumentCaptor<String> msgCaptorCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> roleCaptor = ArgumentCaptor.forClass(String.class);
  
    verify(channelView, times(1)).send(msgCaptorCaptor.capture(), roleCaptor.capture());
  
    // Correct message?
    assertThat(msgCaptorCaptor.getValue()).isEqualTo("The role \"%s\" isn't self-assignable.");
    assertThat(roleCaptor.getValue()).isEqualTo(roleName);
  }
  
  /**
   * Checks whether the command is canceled when the lock couldn't been acquired.
   */
  @Test
  public void testTimeout() throws Exception {
    LoadingCache<Long, Semaphore> activeMembers = Whitebox.getInternalState(AssignCommand.class, ACTIVE_MEMBERS);
    Semaphore semaphore = mock(Semaphore.class);
    
    activeMembers.put(userId, semaphore);
    
    when(semaphore.tryAcquire(anyLong(), any())).thenReturn(false);
    
    assertThrows(TimeoutException.class, () -> command.run());
  }
}
