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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.jrc.client.FailedRequestException;
import zav.jrc.databind.LinkEntity;
import zav.jrc.databind.SubredditEntity;
import zav.jrc.endpoint.subreddit.Subreddit;
import zav.jrc.listener.event.LinkEvent;

/**
 * Checks whether links can be sent via a webhook.
 */
@ExtendWith(MockitoExtension.class)
public class WebhookSubredditListenerTest {
  
  LinkEntity link;
  WebhookSubredditListener listener;
  @Mock JDAWebhookClient client;
  @Mock Subreddit subreddit;
  @Mock SubredditEntity entity;
  
  /**
   * Initializes a new webhook based listener.
   */
  @BeforeEach
  public void setUp() {
    listener = new WebhookSubredditListener(subreddit, client);
    
    link = new LinkEntity();
    link.setOver18(true);
    link.setSpoiler(true);
    link.setAuthor("author");
    link.setSubreddit("subreddit");
  }
  
  @Test
  public void testNotify() throws FailedRequestException {
    when(subreddit.getAbout()).thenReturn(entity);
    listener.notify(new LinkEvent(link));
    verify(client).send(any(WebhookMessage.class));
  }
  
  @Test
  public void testEquality() {
    // Default is 0, so it would match the mocked webhook
    when(client.getId()).thenReturn(Long.MAX_VALUE);
  
    assertEquals(listener, listener);
    assertEquals(listener, new WebhookSubredditListener(subreddit, client));
    assertNotEquals(listener, new Object());
    assertNotEquals(listener, new WebhookSubredditListener(subreddit, mock(JDAWebhookClient.class)));
    assertNotEquals(listener.hashCode(), mock(WebhookSubredditListener.class).hashCode());
  }
}
