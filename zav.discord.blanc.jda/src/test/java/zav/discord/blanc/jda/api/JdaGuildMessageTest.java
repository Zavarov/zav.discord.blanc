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
import static zav.discord.blanc.jda.internal.GuiceUtils.injectGuildMessage;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for checking whether guild messages are properly instantiated using Guice.
 */
public class JdaGuildMessageTest {
  private JdaGuildMessage guildMessage;
  
  /**
   * Initializes {@link #guildMessage} and mocks the required JDA components.
   */
  @BeforeEach
  public void setUp() {
    Message jdaMessage = mock(Message.class);
    
    Member jdaMember = mock(Member.class);
    Guild jdaGuild = mock(Guild.class);
    TextChannel jdaTextChannel = mock(TextChannel.class);
    JDA jda = mock(JDA.class);
    
    when(jdaMessage.getMember()).thenReturn(jdaMember);
    when(jdaMessage.getGuild()).thenReturn(jdaGuild);
    when(jdaMessage.getTextChannel()).thenReturn(jdaTextChannel);
    when(jdaMessage.getJDA()).thenReturn(jda);
    
    when(jdaMember.getUser()).thenReturn(mock(User.class));
    
    guildMessage = injectGuildMessage(jdaMessage);
  }
  
  @Test
  public void testGetAuthor() {
    assertThat(guildMessage.getAuthor()).isNotNull();
  }
  
  @Test
  public void testGetGuild() {
    assertThat(guildMessage.getGuild()).isNotNull();
  }
  
  @Test
  public void testGetMessageChannel() {
    assertThat(guildMessage.getMessageChannel()).isNotNull();
  }
  
  @Test
  public void testGetShard() {
    assertThat(guildMessage.getShard()).isNotNull();
  }
}
