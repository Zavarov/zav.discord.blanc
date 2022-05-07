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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.jrc.databind.LinkEntity;
import zav.jrc.listener.event.LinkEvent;

/**
 * Checks whether links can be sent via a text channel.
 */
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
  
  @Test
  public void testEquality() {
    Set<TextChannelSubredditListener> set = new HashSet<>();
    
    assertTrue(set.add(listener));
    assertFalse(set.add(listener));
    assertTrue(set.add(mock(TextChannelSubredditListener.class)));
  }
}
