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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.requests.RestAction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.databind.GuildDto;
import zav.discord.blanc.databind.TextChannelDto;
import zav.discord.blanc.databind.WebHookDto;
import zav.discord.blanc.db.TextChannelDatabase;
import zav.discord.blanc.db.WebHookDatabase;

/**
 * Checks whether the Reddit job can recover from the persistent database values.
 */
public class RedditJobTest {
  static final Path CHANNEL_DB = Paths.get("TextChannel.db");
  static final Path WEBHOOK_DB = Paths.get("WebHook.db");

  final long guildId = 11111L;
  final long textChannelId = 22222L;
  final long webhookId = 33333L;
  final String subreddit = "subreddit";
  
  @Mock Client client;
  @Mock JDA shard;
  @Mock Guild guild;
  @Mock TextChannel textChannel;
  @Mock Webhook webhook;
  @Mock RestAction<List<Webhook>> action;
  @Mock SubredditObservable observable;
  
  AutoCloseable closeable;
  Injector injector;
  
  /**
   * Creates a database for text channels and webhooks and fills it with dummy values.
   *
   * @throws SQLException In case a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    closeable = openMocks(this);
    injector = Guice.createInjector(new TestModule());
  
    // Set up databases
    TextChannelDatabase.create();
    WebHookDatabase.create();
  
    GuildDto guildDto = new GuildDto();
    guildDto.setId(guildId);
    guildDto.setName("guild");
  
    TextChannelDto textChannelDto = new TextChannelDto();
    textChannelDto.setName("textChannel");
    textChannelDto.setId(textChannelId);
    textChannelDto.setSubreddits(List.of(subreddit));
  
    WebHookDto webHookDto = new WebHookDto();
    webHookDto.setName("webhook");
    webHookDto.setId(webhookId);
    webHookDto.setChannelId(textChannelId);
    webHookDto.setSubreddits(List.of(subreddit));
    
    TextChannelDatabase.put(guildDto, textChannelDto);
    WebHookDatabase.put(guildDto, webHookDto);
    
    // Set up JDA
    when(client.getShards()).thenReturn(List.of(shard));
    when(shard.getGuilds()).thenReturn(List.of(guild));
    
    when(guild.getIdLong()).thenReturn(guildId);
    when(guild.getTextChannelById(textChannelId)).thenReturn(textChannel);
    when(textChannel.retrieveWebhooks()).thenReturn(action);
    when(action.complete()).thenReturn(List.of(webhook));
    when(webhook.getName()).thenReturn("Reddit");
  }
  
  /**
   * Delete all database files.
   *
   * @throws IOException If one of the databases couldn't be deleted.
   */
  @AfterEach
  public void cleanUp() throws Exception {
    delete(CHANNEL_DB);
    delete(WEBHOOK_DB);
    closeable.close();
  }
  
  private void delete(Path db) throws IOException {
    if (Files.exists(db)) {
      Files.delete(db);
    }
  }
  
  @Test
  public void testLoadValuesFromDatabase() throws SQLException {
    injector.getInstance(RedditJob.class);
  
    assertThat(TextChannelDatabase.contains(guildId, textChannelId)).isTrue();
    assertThat(WebHookDatabase.contains(guildId, textChannelId, webhookId)).isTrue();
  }
  
  @Test
  public void testRemoveInvalidTextChannels() throws SQLException {
    when(guild.getTextChannelById(textChannelId)).thenReturn(null);
  
    injector.getInstance(RedditJob.class);
  
    assertThat(TextChannelDatabase.contains(guildId, textChannelId)).isFalse();
    assertThat(WebHookDatabase.contains(guildId, textChannelId, webhookId)).isFalse();
  }
  
  @Test
  public void testRemoveInvalidWebHooks() throws SQLException {
    when(action.complete()).thenReturn(Collections.emptyList());
  
    injector.getInstance(RedditJob.class);
  
    assertThat(TextChannelDatabase.contains(guildId, textChannelId)).isTrue();
    assertThat(WebHookDatabase.contains(guildId, textChannelId, webhookId)).isFalse();
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
    assertThatThrownBy(job::run).isInstanceOf(Error.class);
  }
  
  private class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(SubredditObservable.class).toInstance(observable);
      // Suppress @Inject for the mocked client instance
      bind(Client.class).toProvider(() -> client);
    }
  }
}
