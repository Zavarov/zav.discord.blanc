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
import static zav.discord.blanc.jda.internal.ArgumentImpl.of;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectPrivateChannel;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for checking whether private channels are properly instantiated using Guice.
 */
public class JdaPrivateChannelTest {
  private final long messageId = 11111;
  
  private JdaPrivateChannel privateChannel;
  
  /**
   * Initializes {@link #privateChannel} and mocks the required JDA components.
   */
  @BeforeEach
  public void setUp() {
    PrivateChannel jdaPrivateChannel = mock(PrivateChannel.class);
  
    @SuppressWarnings("unchecked")
    RestAction<Message> jdaMessage = mock(RestAction.class);
    
    when(jdaPrivateChannel.retrieveMessageById(messageId)).thenReturn(jdaMessage);
    when(jdaMessage.complete()).thenReturn(mock(Message.class));
    
    privateChannel = injectPrivateChannel(jdaPrivateChannel);
  }
  
  @Test
  public void testGetMessage() {
    assertThat(privateChannel.getMessage(of(messageId))).isNotNull();
  }
}
