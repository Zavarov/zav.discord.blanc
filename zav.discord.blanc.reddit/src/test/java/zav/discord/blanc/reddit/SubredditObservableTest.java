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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ScheduledExecutorService;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.jrc.client.Client;
import zav.jrc.client.FailedRequestException;
import zav.jrc.listener.observer.SubredditObserver;

/**
 * Checks whether listeners are unique.
 */
@SuppressWarnings("deprecation")
@ExtendWith(MockitoExtension.class)
public class SubredditObservableTest {
  
  @Mock ScheduledExecutorService pool;
  @Mock TextChannel textChannel;
  @Mock Webhook webhook;
  @Mock Client client;
  @Mock SubredditObserver observer;
  
  SubredditObservable observable;
  
  /**
   * Initializes an observable with one text channel and webhook registered.
   */
  @BeforeEach
  public void setUp() {    
    observable = spy(new SubredditObservable(client, pool));
    when(observable.getObserver(anyString())).thenReturn(observer);
  }
  
  @Test
  public void testAddTextChannelListener() {
    observable.addListener("subreddit", textChannel);
  
    verify(observer).addListener(any(TextChannelSubredditListener.class));
  }
  
  @Test
  public void testAddWebhookListener() {
    when(webhook.getToken()).thenReturn(StringUtils.EMPTY);
    
    observable.addListener("subreddit", webhook);
  
    verify(observer).addListener(any(WebhookSubredditListener.class));
  }
  
  @Test
  public void testRemoveTextChannelListener() {
    observable.removeListener("subreddit", textChannel);
  
    verify(observer).removeListener(any(TextChannelSubredditListener.class));
  }
  
  @Test
  public void testRemoveWebhookListener() {
    when(webhook.getToken()).thenReturn(StringUtils.EMPTY);
    
    observable.removeListener("subreddit", webhook);
  
    verify(observer).removeListener(any(WebhookSubredditListener.class));
  }
  
  @Test
  public void testNotifyAll() throws FailedRequestException {
    when(webhook.getToken()).thenReturn(StringUtils.EMPTY);
    
    observable.addListener("subreddit1", webhook);
    observable.addListener("subreddit2", textChannel);
    observable.notifyAllObservers();
    
    verify(observer, times(2)).notifyAllListeners();
  }
  
  /**
   * Use Case: Errors in one observer should not affect any other observer.
   *
   * @throws FailedRequestException Should never be thrown.
   */
  @Test
  public void testNotifyAllWithError() throws FailedRequestException {
    when(webhook.getToken()).thenReturn(StringUtils.EMPTY);
    doThrow(FailedRequestException.class).when(observer).notifyAllListeners();
    
    observable.addListener("subreddit1", webhook);
    observable.addListener("subreddit2", textChannel);
    observable.notifyAllObservers();
    
    verify(observer, times(2)).notifyAllListeners();
  }
}
