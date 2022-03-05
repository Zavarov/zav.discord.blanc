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
import static zav.discord.blanc.db.sql.SqlQuery.ENTITY_DB_PATH;
import static zav.test.io.JsonUtils.read;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import java.nio.file.Files;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebHookEntity;
import zav.discord.blanc.db.TextChannelDatabaseTable;
import zav.discord.blanc.db.WebHookDatabaseTable;

/**
 * Checks whether the Reddit job can recover from the persistent database values.
 */
@ExtendWith(MockitoExtension.class)
public class RedditJobTest {
  
  protected @Mock Client client;
  protected @Mock JDA shard;
  protected @Mock Guild guild;
  protected @Mock TextChannel textChannel;
  protected @Mock Webhook webhook;
  protected @Mock RestAction<List<Webhook>> action;
  protected @Mock SubredditObservable observable;
  
  protected long guildId;
  protected long textChannelId;
  protected long webhookId;
  
  protected TextChannelEntity channelEntity;
  protected WebHookEntity webHookEntity;
  
  protected Injector injector;
  protected TextChannelDatabaseTable textDb;
  protected WebHookDatabaseTable hookDb;
  
  /**
   * Creates a database for text channels and webhooks and fills it with dummy values.
   *
   * @throws SQLException In case a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    injector = Guice.createInjector(new TestModule());
    textDb = injector.getInstance(TextChannelDatabaseTable.class);
    hookDb = injector.getInstance(WebHookDatabaseTable.class);
  
    channelEntity = read("TextChannel.json", TextChannelEntity.class);
    webHookEntity = read("WebHook.json", WebHookEntity.class);
    
    textDb.put(channelEntity);
    hookDb.put(webHookEntity);
    
    guildId = webHookEntity.getGuildId();
    textChannelId = webHookEntity.getChannelId();
    webhookId = webHookEntity.getId();
    
    // Set up JDA
    when(client.getShards()).thenReturn(List.of(shard));
    when(shard.getGuilds()).thenReturn(List.of(guild));
    
    when(guild.getIdLong()).thenReturn(guildId);
    when(guild.getTextChannelById(textChannelId)).thenReturn(textChannel);
  }
  
  /**
   * Delete all database files.
   *
   * @throws IOException If one of the databases couldn't be deleted.
   */
  @AfterEach
  public void cleanUp() throws Exception {
    Files.deleteIfExists(ENTITY_DB_PATH);
    Files.deleteIfExists(ENTITY_DB_PATH.getParent());
  }
  
  @Test
  public void testRemoveInvalidTextChannels() throws SQLException {
    when(guild.getTextChannelById(textChannelId)).thenReturn(null);
  
    injector.getInstance(RedditJob.class);
  
    assertThat(textDb.contains(guildId, textChannelId)).isFalse();
    assertThat(hookDb.contains(guildId, textChannelId, webhookId)).isFalse();
  }
  
  @Test
  public void testRemoveInvalidWebHooks() throws SQLException {
    when(textChannel.retrieveWebhooks()).thenReturn(action);
    when(action.complete()).thenReturn(Collections.emptyList());
  
    injector.getInstance(RedditJob.class);
  
    assertThat(textDb.contains(guildId, textChannelId)).isTrue();
    assertThat(hookDb.contains(guildId, textChannelId, webhookId)).isFalse();
  }
  
  @Test
  public void testRun() {
    when(textChannel.retrieveWebhooks()).thenReturn(action);
    when(action.complete()).thenReturn(List.of(webhook));
    when(webhook.getName()).thenReturn(webHookEntity.getName());
    
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
