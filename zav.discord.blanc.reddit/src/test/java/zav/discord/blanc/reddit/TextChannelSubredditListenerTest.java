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

package zav.discord.blanc.reddit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.jrc.client.FailedRequestException;
import zav.jrc.databind.LinkEntity;
import zav.jrc.listener.event.LinkEvent;

/**
 * Checks whether links can be sent via a text channel.
 */
@Deprecated
public class TextChannelSubredditListenerTest {
  
  LinkEntity link;
  TextChannelSubredditListener listener;
  TextChannel textChannel;
  MessageAction action;
  
  /**
   * Initializes a new text-channel based listener.
   */
  @BeforeEach
  public void setUp() {
    textChannel = mock(TextChannel.class);
    action = mock(MessageAction.class);
    listener = new TextChannelSubredditListener(textChannel);

    link = new LinkEntity();
    link.setOver18(true);
    link.setSpoiler(true);
    link.setAuthor("author");

    when(textChannel.sendMessage(any(Message.class))).thenReturn(action);
  }

  @Test
  public void testNotify() {
    listener.notify(new LinkEvent(link));
    verify(action, times(1)).complete();
  }
  
  /**
   * Use Case: Errors when sending a link should be suppressed and logged as to not affect any other
   * text-channel notifications. 
   *
   * @throws FailedRequestException Should never be thrown.
   */
  @Test
  public void testNotifyWithError() throws FailedRequestException {
    when(textChannel.sendMessage(any(Message.class))).thenThrow(RuntimeException.class);
    listener.notify(new LinkEvent(link));
  }
  
  @Test
  public void testEquality() {
    // Default is 0, so it would match the mocked text channel
    when(textChannel.getIdLong()).thenReturn(Long.MAX_VALUE);
    
    assertEquals(listener, listener);
    assertEquals(listener, new TextChannelSubredditListener(textChannel));
    assertNotEquals(listener, new Object());
    assertNotEquals(listener, new TextChannelSubredditListener(mock(TextChannel.class)));
    assertNotEquals(listener.hashCode(), mock(TextChannelSubredditListener.class).hashCode());
  }
}
