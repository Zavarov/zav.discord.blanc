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

package zav.discord.blanc.runtime.command.mod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.runtime.command.AbstractDatabaseTest;

/**
 * Check whether subreddits can be added and removed from the Reddit feed.
 */
@ExtendWith(MockitoExtension.class)
public class RedditRemoveCommandTest extends AbstractDatabaseTest<WebhookEntity> {
  @Mock OptionMapping name;
  @Mock OptionMapping index;
  GuildCommandManager manager;
  TextChannelEntity channelEntity;
  RedditRemoveCommand command;
  GuildEntity guildEntity;
  
  /**
   * Initializes the command with no arguments.
   */
  @BeforeEach
  public void setUp() {
    super.setUp(new WebhookEntity());
    guildEntity = new GuildEntity();
    channelEntity = new TextChannelEntity();
    
    when(entityManager.find(eq(WebhookEntity.class), any())).thenReturn(entity);
    when(entityManager.find(eq(GuildEntity.class), any())).thenReturn(guildEntity);
    when(entityManager.find(eq(TextChannelEntity.class), any())).thenReturn(channelEntity);

    manager = new GuildCommandManager(client, event);
    command = new RedditRemoveCommand(event, manager);
  }
  
  /**
   * Subreddits are case insensitive.
   *
   * @param subredditName A human-readable subreddit name.
   */
  @ParameterizedTest
  @ValueSource(strings = { "RedditDev", "redditDev", "ReDdItDeV", "redditdev" })
  public void testRemoveSubredditByName(String subredditName) throws Exception {
    when(event.getOption("name")).thenReturn(name);
    when(event.getOption("index")).thenReturn(null);
    when(name.getAsString()).thenReturn(subredditName);
    entity.setSubreddits(Lists.newArrayList("redditdev"));
    
    command.run();

    // Has the database been updated?
    assertEquals(entity.getSubreddits(), Collections.emptyList());
    assertNotNull(entity.getChannel());
    assertNotNull(entity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(eq("redditdev"), any(Webhook.class));
  }
  
  @Test
  public void testRemoveSubredditByIndex() throws Exception {
    when(event.getOption("name")).thenReturn(null);
    when(event.getOption("index")).thenReturn(index);
    when(index.getAsLong()).thenReturn(0L);
    entity.setSubreddits(Lists.newArrayList("redditdev"));
    
    command.run();

    // Has the database been updated?
    assertEquals(entity.getSubreddits(), Collections.emptyList());
    assertNotNull(entity.getChannel());
    assertNotNull(entity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(eq("redditdev"), any(Webhook.class));
  }
  
  /**
   * Subreddits are case insensitive.
   *
   * @param subredditName A human-readable subreddit name.
   */
  @ParameterizedTest
  @ValueSource(strings = { "RedditDev", "redditDev", "ReDdItDeV", "redditdev" })
  public void testRemoveByNameAndDeleteWebhook(String subredditName) throws Exception {
    when(event.getOption("name")).thenReturn(name);
    when(event.getOption("index")).thenReturn(null);
    when(webhook.delete()).thenReturn(delete);
    when(name.getAsString()).thenReturn(subredditName);
    entity.setSubreddits(Lists.newArrayList("redditdev"));
    entity.setOwner(true);
    
    command.run();

    // Has the database been updated?
    assertEquals(entity.getSubreddits(), Collections.emptyList());
    assertNotNull(entity.getChannel());
    assertNotNull(entity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(anyString(), any(Webhook.class));
    // Has the webhook been deleted
    verify(delete).complete();
  }
  
  @Test
  public void testRemoveByIndexAndDeleteWebhook() throws Exception {
    when(event.getOption("name")).thenReturn(null);
    when(event.getOption("index")).thenReturn(index);
    when(webhook.delete()).thenReturn(delete);
    when(index.getAsLong()).thenReturn(0L);
    entity.setSubreddits(Lists.newArrayList("redditdev"));
    entity.setOwner(true);
    
    command.run();

    // Has the database been updated?
    assertEquals(entity.getSubreddits(), Collections.emptyList());
    assertNotNull(entity.getChannel());
    assertNotNull(entity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(anyString(), any(Webhook.class));
    // Has the webhook been deleted
    verify(delete).complete();
  }
  
  @Test
  public void testRemoveByNameAndKeepWebhook() throws Exception {
    when(event.getOption("name")).thenReturn(name);
    when(event.getOption("index")).thenReturn(null);
    when(name.getAsString()).thenReturn("redditdev");
    entity.setSubreddits(Lists.newArrayList("redditdev", "boiledgoulash"));
    entity.setOwner(true);
    
    command.run();

    // Has the database been updated?
    assertEquals(entity.getSubreddits(), Lists.newArrayList("boiledgoulash"));
    assertNotNull(entity.getChannel());
    assertNotNull(entity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(anyString(), any(Webhook.class));
    // Does the webhook still exists?
    verify(delete, times(0)).complete();
  }
  
  @Test
  public void testRemoveByIndexAndKeepWebhook() throws Exception {
    when(event.getOption("name")).thenReturn(null);
    when(event.getOption("index")).thenReturn(index);
    when(index.getAsLong()).thenReturn(0L);
    entity.setSubreddits(Lists.newArrayList("redditdev", "boiledgoulash"));
    entity.setOwner(true);
    
    command.run();

    // Has the database been updated?
    assertEquals(entity.getSubreddits(), Lists.newArrayList("boiledgoulash"));
    assertNotNull(entity.getChannel());
    assertNotNull(entity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(anyString(), any(Webhook.class));
    // Does the webhook still exists?
    verify(delete, times(0)).complete();
  }
}
