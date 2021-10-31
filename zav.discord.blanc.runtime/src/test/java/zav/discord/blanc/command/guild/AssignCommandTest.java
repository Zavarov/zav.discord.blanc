package zav.discord.blanc.command.guild;

import com.google.common.cache.LoadingCache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import zav.discord.blanc.command.AbstractCommandTest;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.databind.Role;
import zav.discord.blanc.runtime.command.guild.AssignCommand;
import zav.discord.blanc.view.RoleView;

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
  private Role roleMock;
  private RoleView roleViewMock;
  
  @Captor
  private ArgumentCaptor<Collection<Role>> addedRoles;
  
  @Captor
  private ArgumentCaptor<Collection<Role>> removedRoles;
  
  @Captor
  private ArgumentCaptor<Role> removedRole;
  
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    
    command = parse("b:assign %s", roleId);
    
    // User may already have a role in the same group
    roleMock = mock(Role.class);
    roleViewMock = mock(RoleView.class);
    when(roleMock.getGroup()).thenReturn(roleGroup);
    when(roleViewMock.getAbout()).thenReturn(roleMock);
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
    when(memberView.getRoles()).thenReturn(Set.of(roleViewMock));
    
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
    verify(memberView, times(1)).modifyRoles(addedRoles.capture(), removedRoles.capture());
    
    assertThat(addedRoles.getValue()).containsExactly(role);
    assertThat(removedRoles.getValue()).containsExactly(roleMock);
  }
  
  /**
   * Checks whether the role could be removed from the author.
   */
  @Test
  public void testRemoveRole() throws Exception {
    // User has the given role assigned => remove
    when(memberView.getRoles()).thenReturn(Set.of(roleView));
  
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
    verify(memberView, times(1)).removeRole(removedRole.capture());
  
    assertThat(removedRole.getValue()).isEqualTo(role);
  }
  
  /**
   * Checks whether the role couldn't be modified, because it is un-assignable.
   */
  @Test
  public void testInvalidRole() throws Exception {
    // Role is not assignable
    role.setGroup(null);
    when(memberView.getRoles()).thenReturn(Set.of(roleView));
  
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
