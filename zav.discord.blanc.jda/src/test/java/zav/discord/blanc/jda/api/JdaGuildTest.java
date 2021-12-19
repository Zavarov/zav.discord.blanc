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
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static zav.discord.blanc.jda.internal.ArgumentImpl.of;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectGuild;

import java.util.Collection;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for checking whether guilds are properly instantiated using Guice.
 */
public class JdaGuildTest {
  private final long roleId = 11111;
  private final long memberId = 22222;
  private final long textChannelId = 33333;
  
  private JdaGuild guild;
  
  /**
   * Initializes {@link #guild} and mocks the required JDA components.
   */
  @BeforeEach
  public void setUp() throws Exception {
    Guild jdaGuild = mock(Guild.class);
    
    Member jdaMember = mock(Member.class);
    Member jdaSelfMember = mock(Member.class);
    Role jdaRole = mock(Role.class);
    TextChannel jdaTextChannel = mock(TextChannel.class);
    
    when(jdaGuild.getSelfMember()).thenReturn(jdaSelfMember);
    when(jdaGuild.getRoleById(eq(roleId))).thenReturn(jdaRole);
    when(jdaGuild.getMemberById(memberId)).thenReturn(jdaMember);
    when(jdaGuild.getTextChannelById(textChannelId)).thenReturn(jdaTextChannel);
    when(jdaGuild.getRoles()).thenReturn(List.of(jdaRole));
    when(jdaGuild.getMembers()).thenReturn(List.of(jdaMember, jdaSelfMember));
    when(jdaGuild.getTextChannels()).thenReturn(List.of(jdaTextChannel));
  
    when(jdaMember.getUser()).thenReturn(mock(User.class));
    when(jdaSelfMember.getUser()).thenReturn(mock(User.class));
    
    guild = injectGuild(jdaGuild);
  }
  
  @Test
  public void testGetSelfMember() {
    assertThat(guild.getSelfMember()).isNotNull();
  }
  
  @Test
  public void testGetRole() {
    assertThat(guild.getRole(of(roleId))).isNotNull();
  }
  
  @Test
  public void testGetMember() {
    assertThat(guild.getMember(of(memberId))).isNotNull();
  }
  
  @Test
  public void testGetTextChannel() {
    assertThat(guild.getTextChannel(of(textChannelId))).isNotNull();
  }
  
  @Test
  public void testGetRoles() {
    Collection<JdaRole> roles = guild.getRoles();
    
    assertThat(roles).hasSize(1);
    assertThat(roles).doesNotContainNull();
  }
  
  @Test
  public void testGetMembers() {
    Collection<JdaMember> members = guild.getMembers();
  
    assertThat(members).hasSize(2);
    assertThat(members).doesNotContainNull();
  }
  
  @Test
  public void testGetTextChannels() {
    Collection<JdaTextChannel> textChannels = guild.getTextChannels();
  
    assertThat(textChannels).hasSize(1);
    assertThat(textChannels).doesNotContainNull();
  }
}
