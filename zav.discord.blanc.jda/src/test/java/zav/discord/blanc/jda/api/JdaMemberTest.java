/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.jda.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static zav.discord.blanc.api.Permission.MANAGE_MESSAGES;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectMember;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for checking whether guild members are properly instantiated using Guice.
 */
public class JdaMemberTest {
  private JdaMember member;
  
  /**
   * Initializes {@link #member} and mocks the required JDA components.
   */
  @BeforeEach
  public void setUp() {
    Member jdaMember = mock(Member.class);
    
    when(jdaMember.getUser()).thenReturn(mock(User.class));
    when(jdaMember.getRoles()).thenReturn(List.of(mock(Role.class)));
    when(jdaMember.getPermissions()).thenReturn(EnumSet.of(Permission.MESSAGE_MANAGE));
    
    member = injectMember(jdaMember);
  }
  
  @Test
  public void testGetRoles() {
    Set<JdaRole> roles = member.getRoles();
    
    assertThat(roles).hasSize(1);
    assertThat(roles).doesNotContainNull();
  }
  
  @Test
  public void testGetPermissions() {
    assertThat(member.getPermissions()).containsExactly(MANAGE_MESSAGES);
  }
}