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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectTextChannel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.jda.AbstractTest;

/**
 * JUnit test for checking whether text channels are properly instantiated using Guice.
 */
public class JdaTextChannelTest extends AbstractTest {
  private JdaTextChannel textChannel;
  
  /**
   * Initializes {@link #textChannel} and mocks the required JDA components.
   */
  @BeforeEach
  public void setUp() {
    textChannel = injectTextChannel(jdaTextChannel);
  }
  
  @Test
  public void testGetMessage() {
    assertThat(textChannel.getMessage(messageId)).isNotNull();
  }
  
  @Test
  public void testGetExistingWebHook() {
    assertThat(textChannel.getWebHook(webHookName, true)).isNotNull();
    
    verify(jdaTextChannel, times(0)).createWebhook(any());
  }
  
  @Test
  public void testGetNewWebHook() {
    // Name should differ s.t. a new web hook is created
    when(jdaWebHook.getName()).thenReturn("foo");
    
    assertThat(textChannel.getWebHook(webHookName, true)).isNotNull();
    
    verify(jdaTextChannel, times(1)).createWebhook(any());
  }
  
  @Test
  public void testGetInvalidWebHook() {
    // Name should differ s.t. a new web hook is created
    when(jdaWebHook.getName()).thenReturn("foo");
    
    assertThrows(RuntimeException.class, () -> textChannel.getWebHook(webHookName, false));
  
    verify(jdaTextChannel, times(0)).createWebhook(any());
  }
}
