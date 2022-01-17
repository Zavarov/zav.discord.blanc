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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.inject.Injector;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import zav.jrc.api.Reddit;
import zav.jrc.api.Subreddit;
import zav.jrc.client.FailedRequestException;
import zav.jrc.listener.observer.SubredditObserver;

public class SubredditObservableTest {
  
  final long textChannel1Id = 11111L;
  final long textChannel2Id = 22222L;
  final long webhook1Id = 33333L;
  final long webhook2Id = 44444L;
  
  @Mock Injector injector;
  @Mock Reddit reddit;
  @Mock Subreddit subreddit;
  @Mock TextChannel textChannel1;
  @Mock TextChannel textChannel2;
  @Mock Webhook webhook1;
  @Mock Webhook webhook2;
  
  AutoCloseable closeable;
  SubredditObservable observable;
  
  @BeforeEach
  public void setUp() {
    closeable = openMocks(this);
  
    when(injector.getInstance(Reddit.class)).thenReturn(reddit);
    when(reddit.getSubreddit(any())).thenReturn(subreddit);
    when(textChannel1.getIdLong()).thenReturn(textChannel1Id);
    when(textChannel2.getIdLong()).thenReturn(textChannel2Id);
    when(webhook1.getIdLong()).thenReturn(webhook1Id);
    when(webhook2.getIdLong()).thenReturn(webhook2Id);
    
    observable = spy(new SubredditObservable());
    observable.setInjector(injector);
    observable.addListener("subreddit", textChannel1);
    observable.addListener("subreddit", webhook1);
  }
  
  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }
  
  @Test
  public void testAddTextChannelListener() {
    assertThat(observable.addListener("subreddit", textChannel1)).isFalse();
    assertThat(observable.addListener("SUBREDDIT", textChannel1)).isFalse();
    assertThat(observable.addListener("XXXXXXXXX", textChannel1)).isTrue();
    assertThat(observable.addListener("subreddit", textChannel2)).isTrue();
  }
  
  @Test
  public void testAddWebhookListener() {
    assertThat(observable.addListener("subreddit", webhook1)).isFalse();
    assertThat(observable.addListener("SUBREDDIT", webhook1)).isFalse();
    assertThat(observable.addListener("XXXXXXXXX", webhook1)).isTrue();
    assertThat(observable.addListener("subreddit", webhook2)).isTrue();
  }
  
  @Test
  public void testRemoveTextChannelListener() {
    assertThat(observable.removeListener("SUBREDDIT", textChannel1)).isTrue();
    assertThat(observable.removeListener("subreddit", textChannel1)).isFalse();
    assertThat(observable.removeListener("XXXXXXXXX", textChannel1)).isFalse();
    assertThat(observable.removeListener("SUBREDDIT", textChannel2)).isFalse();
  }
  
  @Test
  public void testRemoveWebhookListener() {
    assertThat(observable.removeListener("SUBREDDIT", webhook1)).isTrue();
    assertThat(observable.removeListener("subreddit", webhook1)).isFalse();
    assertThat(observable.removeListener("XXXXXXXXX", webhook1)).isFalse();
    assertThat(observable.removeListener("subreddit", webhook2)).isFalse();
  }
  
  @Test
  public void testNotifyAllObservers() {
    observable.notifyAllObservers();
  }
  
  @Test
  public void testNotifyAllObserversWithException() {
    try (MockedConstruction<SubredditObserver> ignored = mockConstruction(SubredditObserver.class, (t, c) ->
          doThrow(FailedRequestException.wrap(null)).when(t).notifyAllListeners())
    ) {
      // Should always throw a FailedRequestException
      observable.addListener("exception", webhook1);
      observable.notifyAllObservers();
    }
  }
}
