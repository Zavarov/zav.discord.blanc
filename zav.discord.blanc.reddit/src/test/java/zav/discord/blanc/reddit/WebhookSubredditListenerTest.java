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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookMessage;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
  @Captor ArgumentCaptor<WebhookMessage> captor;
  @Mock JDAWebhookClient client;
  @Mock JDAWebhookClient client2;
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
  
  /**
   * Use Case: Errors when sending a link should be suppressed and logged as to not affect any other
   * webhook notifications. 
   *
   * @throws FailedRequestException Should never be thrown.
   */
  @Test
  public void testNotifyWithError() throws FailedRequestException {
    when(subreddit.getAbout()).thenReturn(entity);
    when(client.send(any(WebhookMessage.class))).thenThrow(RuntimeException.class);
    listener.notify(new LinkEvent(link));
  }
  
  /**
   * Use Case: The subreddit icon should be used as the image of the message author.
   *
   * @param url The effective community icon.
   * @param expected The icon used in the Discord message.
   * @throws FailedRequestException Should never be thrown.
   */
  @MethodSource
  @ParameterizedTest
  public void testNotifyAvatarUrl(String url, String expected) throws FailedRequestException {
    when(subreddit.getAbout()).thenReturn(entity);
    when(entity.getIconImage()).thenReturn(url);
    listener.notify(new LinkEvent(link));
    verify(client).send(captor.capture());
    assertEquals(expected, captor.getValue().getAvatarUrl());
  }
  
  /**
   * Provider for {@link #testNotifyAvatarUrl(String, String)}.
   *
   * @return A stream of all key-value pairs used as arguments for the unit test.
   */
  public static Stream<Arguments> testNotifyAvatarUrl() {
    return Stream.of(
          Arguments.of(null, null),
          Arguments.of("test", null),
          Arguments.of("https://www.test.com/image.jpg", "https://www.test.com/image.jpg")
    );
  }
  
  /**
   * Use Case: The community icon should be used as the image of the message author if the
   * avatar url is {@code null}.
   *
   * @param url The effective community icon.
   * @param expected The icon used in the Discord message.
   * @throws FailedRequestException Should never be thrown.
   */
  @MethodSource
  @ParameterizedTest
  public void testNotifyCommunityUrl(String url, String expected) throws FailedRequestException {
    when(subreddit.getAbout()).thenReturn(entity);
    when(entity.getCommunityIcon()).thenReturn(url);
    listener.notify(new LinkEvent(link));
    verify(client).send(captor.capture());
    assertEquals(expected, captor.getValue().getAvatarUrl());
  }
  
  /**
   * Provider for {@link #testNotifyCommunityUrl(String, String)}.
   *
   * @return A stream of all key-value pairs used as arguments for the unit test.
   */
  public static Stream<Arguments> testNotifyCommunityUrl() {
    return Stream.of(
          Arguments.of(null, null),
          Arguments.of("test", null),
          Arguments.of("https://www.test.com/image.jpg", "https://www.test.com/image.jpg")
    );
  }
  
  @Test
  public void testNotifyUnavailableSubreddit() throws FailedRequestException {
    when(subreddit.getAbout()).thenThrow(FailedRequestException.wrap(new Exception()));
    listener.notify(new LinkEvent(link));
    verify(client).send(captor.capture());
    assertNull(captor.getValue().getAvatarUrl());
  }
  
  @Test
  public void testEquality() {
    // Default is 0, so it would match the mocked webhook
    when(client.getId()).thenReturn(Long.MAX_VALUE);
  
    assertEquals(listener, listener);
    assertEquals(listener, new WebhookSubredditListener(subreddit, client));
    assertNotEquals(listener, new Object());
    assertNotEquals(listener, new WebhookSubredditListener(subreddit, client2));
    assertNotEquals(listener.hashCode(), mock(WebhookSubredditListener.class).hashCode());
  }
}
