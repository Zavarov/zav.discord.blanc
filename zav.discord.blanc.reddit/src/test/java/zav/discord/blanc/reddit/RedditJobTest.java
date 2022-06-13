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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.sql.SQLException;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.db.WebhookTable;

/**
 * Checks whether the Reddit job can recover from the persistent database values.
 */
@ExtendWith(MockitoExtension.class)
public class RedditJobTest {
  
  @Mock Client client;
  @Mock JDA jda;
  @Mock Guild guild;
  @Mock TextChannel textChannel;
  @Mock SubredditObservable observable;
  @Mock TextChannelTable textDb;
  @Mock TextChannelInitializer textInitializer;
  @Mock WebhookTable hookDb;
  @Mock WebhookInitializer hookInitializer;
  
  Injector injector;
  RedditJob job;
  
  /**
   * Creates a database for text channels and webhooks and fills it with dummy values.
   *
   * @throws SQLException In case a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    injector = Guice.createInjector(new TestModule());
    job = injector.getInstance(RedditJob.class);
  }
  
  @Test
  public void testPostConstruct() throws SQLException {
    when(client.getShards()).thenReturn(List.of(jda));
    when(jda.getGuilds()).thenReturn(List.of(guild));
    when(guild.getTextChannels()).thenReturn(List.of(textChannel));
    
    job.postConstruct(client, textInitializer);
    job.postConstruct(client, hookInitializer);
   
    // User can't talk in the channel
    verify(hookInitializer, times(0)).load(any());
    verify(textInitializer).load(any());
  
    when(textChannel.canTalk()).thenReturn(true);
    job.postConstruct(client, hookInitializer);
    verify(hookInitializer).load(any());
  }
  
  @Test
  public void testRun() {
    RedditJob job = injector.getInstance(RedditJob.class);
    
    job.run();
  
    // Catch all exceptions to prevent the job from ending prematurely
    doThrow(new RuntimeException()).when(observable).notifyAllObservers();
    job.run();
  
    // We can't recover from errors -> terminate
    doThrow(new Error()).when(observable).notifyAllObservers();
    assertThrows(Error.class, job::run);
  }
  
  private class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(Client.class).toProvider(() -> client);
      bind(SubredditObservable.class).toInstance(observable);
      bind(TextChannelTable.class).toInstance(textDb);
      bind(WebhookTable.class).toInstance(hookDb);
    }
  }
}
