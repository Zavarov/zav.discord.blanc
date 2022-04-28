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
import static org.mockito.Mockito.when;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.jrc.client.Client;
import zav.jrc.http.RestRequest;
import zav.jrc.listener.requester.LinkRequester;

/**
 * Checks whether listeners are unique.
 */
@ExtendWith(MockitoExtension.class)
public class SubredditObservableTest {
  
  final long textChannel1Id = 11111L;
  final long textChannel2Id = 22222L;
  final long webhook1Id = 33333L;
  final long webhook2Id = 44444L;
  
  @Mock LinkRequester requester;
  @Mock Client client;
  @Mock TextChannel textChannel1;
  @Mock TextChannel textChannel2;
  @Mock Webhook webhook1;
  @Mock Webhook webhook2;
  
  SubredditObservable observable;
  Injector injector;
  
  /**
   * Initializes an observable with one text channel and webhook registered.
   */
  @BeforeEach
  public void setUp() {
    new RestRequest.Builder();
    injector = Guice.createInjector(new TestModule());
    observable = injector.getInstance(SubredditObservable.class);
    //observable = spy(new SubredditObservable());
    // observable.setInjector(injector);
    observable.addListener("subreddit", textChannel1);
    observable.addListener("subreddit", webhook1);
  }
  
  @Test
  public void testAddTextChannelListener() {
    when(textChannel1.getIdLong()).thenReturn(textChannel1Id);
    when(textChannel2.getIdLong()).thenReturn(textChannel2Id);
    
    assertThat(observable.addListener("subreddit", textChannel1)).isFalse();
    assertThat(observable.addListener("SUBREDDIT", textChannel1)).isFalse();
    assertThat(observable.addListener("XXXXXXXXX", textChannel1)).isTrue();
    assertThat(observable.addListener("subreddit", textChannel2)).isTrue();
  }
  
  @Test
  public void testAddWebhookListener() {
    when(webhook1.getIdLong()).thenReturn(webhook1Id);
    when(webhook2.getIdLong()).thenReturn(webhook2Id);
    
    assertThat(observable.addListener("subreddit", webhook1)).isFalse();
    assertThat(observable.addListener("SUBREDDIT", webhook1)).isFalse();
    assertThat(observable.addListener("XXXXXXXXX", webhook1)).isTrue();
    assertThat(observable.addListener("subreddit", webhook2)).isTrue();
  }
  
  @Test
  public void testRemoveTextChannelListener() {
    when(textChannel1.getIdLong()).thenReturn(textChannel1Id);
    
    assertThat(observable.removeListener("SUBREDDIT", textChannel1)).isTrue();
    assertThat(observable.removeListener("subreddit", textChannel1)).isFalse();
    assertThat(observable.removeListener("XXXXXXXXX", textChannel1)).isFalse();
    assertThat(observable.removeListener("SUBREDDIT", textChannel2)).isFalse();
  }
  
  @Test
  public void testRemoveWebhookListener() {
    when(webhook1.getIdLong()).thenReturn(webhook1Id);
  
    assertThat(observable.removeListener("SUBREDDIT", webhook1)).isTrue();
    assertThat(observable.removeListener("subreddit", webhook1)).isFalse();
    assertThat(observable.removeListener("XXXXXXXXX", webhook1)).isFalse();
    assertThat(observable.removeListener("subreddit", webhook2)).isFalse();
  }
  
  private class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(Client.class).toInstance(client);
    }
  }
}
