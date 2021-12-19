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
import static zav.discord.blanc.jda.internal.GuiceUtils.injectShard;

import java.util.Collection;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for checking whether shards are properly instantiated using Guice.
 */
public class JdaShardTest {
  private final long guildId = 11111;
  private final long userId = 22222;
  
  private JdaShard shard;
  
  /**
   * Initializes {@link #shard} and mocks the required JDA components.
   */
  @BeforeEach
  public void setUp() {
    JDA jda = mock(JDA.class);
  
    @SuppressWarnings("unchecked")
    RestAction<User> jdaUser = mock(RestAction.class);
    
    when(jda.getSelfUser()).thenReturn(mock(SelfUser.class));
    when(jda.getGuilds()).thenReturn(List.of(mock(Guild.class)));
    when(jda.getGuildById(eq(guildId))).thenReturn(mock(Guild.class));
    when(jda.retrieveUserById(eq(userId))).thenReturn(jdaUser);
    when(jdaUser.complete()).thenReturn(mock(User.class));
    
    shard = injectShard(jda);
  }
  
  @Test
  public void testGetSelfUser() {
    assertThat(shard.getSelfUser()).isNotNull();
  }
  
  @Test
  public void testGetGuild() {
    assertThat(shard.getGuild(of(guildId))).isNotNull();
  }
  
  @Test
  public void testGetUser() {
    assertThat(shard.getUser(of(userId))).isNotNull();
  }
  
  @Test
  public void testGetGuilds() {
    Collection<JdaGuild> guilds = shard.getGuilds();
    
    assertThat(guilds).hasSize(1);
    assertThat(guilds).doesNotContainNull();
  }
}
