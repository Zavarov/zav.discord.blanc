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
import static zav.discord.blanc.jda.internal.GuiceUtils.injectPrivateMessage;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for checking whether private messages are properly instantiated using Guice.
 */
public class JdaPrivateMessageTest {
  private JdaPrivateMessage privateMessage;
  
  /**
   * Initializes {@link #privateMessage} and mocks the required JDA components.
   */
  @BeforeEach
  public void setUp() {
    Message jdaPrivateMessage = mock(Message.class);
    
    when(jdaPrivateMessage.getPrivateChannel()).thenReturn(mock(PrivateChannel.class));
    when(jdaPrivateMessage.getJDA()).thenReturn(mock(JDA.class));
    when(jdaPrivateMessage.getAuthor()).thenReturn(mock(User.class));
    
    privateMessage = injectPrivateMessage(jdaPrivateMessage);
  }
  
  @Test
  public void testGetMessageChannel() {
    assertThat(privateMessage.getMessageChannel()).isNotNull();
  }
  
  @Test
  public void testGetAuthor() {
    assertThat(privateMessage.getAuthor()).isNotNull();
  }
  
  @Test
  public void testGetShard() {
    assertThat(privateMessage.getShard()).isNotNull();
  }
}
