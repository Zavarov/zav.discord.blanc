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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Check whether subreddits can be added and removed from the Reddit feed.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RedditRemoveCommandTest extends AbstractTest {
  @Mock OptionMapping name;
  @Mock OptionMapping index;
  GuildCommandManager manager;
  RedditRemoveCommand command;
  
  /**
   * Initializes the command with no arguments.
   */
  @BeforeEach
  public void setUp() {
    when(webhook.getName()).thenReturn(AbstractRedditCommand.WEBHOOK);

    manager = new GuildCommandManager(shard, event);
    command = new RedditRemoveCommand(event, manager);
  }
  
  /**
   * Subreddits are case insensitive.
   *
   * @param subredditName
   *          A human-readable subreddit name.
   */
  @ParameterizedTest
  @ValueSource(strings = { "RedditDev", "redditDev", "ReDdItDeV", "redditdev" })
  public void testRemoveSubredditByName(String subredditName) throws Exception {
    when(event.getOption("name")).thenReturn(name);
    when(event.getOption("index")).thenReturn(null);
    when(name.getAsString()).thenReturn(subredditName);
    webhookEntity.setSubreddits(new ArrayList<>(List.of("redditdev", "all")));
    
    command.run();

    // Has the database been updated?
    assertEquals(webhookEntity.getSubreddits(), Collections.singletonList("all"));
    assertNotNull(webhookEntity.getChannel());
    assertNotNull(webhookEntity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(eq("redditdev"), any(Webhook.class));
  }
  
  @Test
  public void testRemoveSubredditByUnknownName() {
    when(event.getOption("name")).thenReturn(name);
    when(event.getOption("index")).thenReturn(null);
    when(name.getAsString()).thenReturn("all");
    webhookEntity.setSubreddits(new ArrayList<>(List.of("redditdev")));
    
    command.run();
    
    // The Reddit job should not've been updated
    verify(subredditObservable, times(0)).removeListener(anyString(), any(Webhook.class));
    assertEquals(webhookEntity.getSubreddits(), List.of("redditdev"));
  }
  
  @Test
  public void testRemoveSubredditByIndex() throws Exception {
    when(event.getOption("name")).thenReturn(null);
    when(event.getOption("index")).thenReturn(index);
    when(index.getAsLong()).thenReturn(0L);
    webhookEntity.setSubreddits(new ArrayList<>(List.of("redditdev", "all")));
    
    command.run();

    // Has the database been updated?
    assertEquals(webhookEntity.getSubreddits(), Collections.singletonList("all"));
    assertNotNull(webhookEntity.getChannel());
    assertNotNull(webhookEntity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(eq("redditdev"), any(Webhook.class));
  }
  
  @Test
  public void testRemoveSubredditByIndexTooLow() throws Exception {
    when(event.getOption("name")).thenReturn(null);
    when(event.getOption("index")).thenReturn(index);
    when(index.getAsLong()).thenReturn(-1L);
    
    channelEntity.setSubreddits(new ArrayList<>(List.of("redditdev")));
    
    command.run();
    
    assertEquals(channelEntity.getSubreddits(), List.of("redditdev"));
    verify(subredditObservable, times(0)).removeListener(anyString(), any(Webhook.class));
  }
  
  @Test
  public void testRemoveSubredditByIndexTooHigh() throws Exception {
    when(event.getOption("name")).thenReturn(null);
    when(event.getOption("index")).thenReturn(index);
    when(index.getAsLong()).thenReturn(Long.MAX_VALUE);
    
    channelEntity.setSubreddits(new ArrayList<>(List.of("redditdev")));
    
    command.run();
    
    assertEquals(channelEntity.getSubreddits(), List.of("redditdev"));
    verify(subredditObservable, times(0)).removeListener(anyString(), any(Webhook.class));
  }
  
  @Test
  public void testRemoveSubredditByIndexNoSubreddit() throws Exception {
    when(event.getOption("name")).thenReturn(null);
    when(event.getOption("index")).thenReturn(index);
    
    channelEntity.setSubreddits(new ArrayList<>());
    
    command.run();
    
    assertTrue(channelEntity.isEmpty());
    verify(subredditObservable, times(0)).removeListener(anyString(), any(Webhook.class));
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
    webhookEntity.setSubreddits(new ArrayList<>(List.of("redditdev")));
    
    command.run();

    // Has the database been updated?
    assertEquals(webhookEntity.getSubreddits(), Collections.emptyList());
    assertNull(webhookEntity.getChannel());
    assertNull(webhookEntity.getGuild());
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
    webhookEntity.setSubreddits(new ArrayList<>(List.of("redditdev")));
    
    command.run();

    // Has the database been updated?
    assertEquals(webhookEntity.getSubreddits(), Collections.emptyList());
    assertNull(webhookEntity.getChannel());
    assertNull(webhookEntity.getGuild());
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
    webhookEntity.setSubreddits(new ArrayList<>(List.of("redditdev", "boiledgoulash")));
    
    command.run();

    // Has the database been updated?
    assertEquals(webhookEntity.getSubreddits(), List.of("boiledgoulash"));
    assertNotNull(webhookEntity.getChannel());
    assertNotNull(webhookEntity.getGuild());
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
    webhookEntity.setSubreddits(new ArrayList<>(List.of("redditdev", "boiledgoulash")));
    
    command.run();

    // Has the database been updated?
    assertEquals(webhookEntity.getSubreddits(), List.of("boiledgoulash"));
    assertNotNull(webhookEntity.getChannel());
    assertNotNull(webhookEntity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(anyString(), any(Webhook.class));
    // Does the webhook still exists?
    verify(delete, times(0)).complete();
  }
  
  /**
   * Use Case: Deleting the last subreddit should remove the entity from the database.
   */
  public void testRemoveLastSubredditByName() {
    when(event.getOption("name")).thenReturn(name);
    when(event.getOption("index")).thenReturn(null);
    when(name.getAsString()).thenReturn("redditdev");
    when(webhook.delete()).thenReturn(delete);
    webhookEntity.setSubreddits(new ArrayList<>(List.of("redditdev")));
    
    command.run();
    
    // Has the database been updated?
    assertEquals(webhookEntity.getSubreddits(), Collections.emptyList());
    assertNull(webhookEntity.getChannel());
    assertNull(webhookEntity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(eq("redditdev"), any(Webhook.class));
    verify(delete).complete();
  }
  
  /**
   * Use Case: Deleting the last subreddit should remove the entity from the database.
   */
  @Test
  public void testRemoveLastSubredditByIndex() {
    when(event.getOption("name")).thenReturn(null);
    when(event.getOption("index")).thenReturn(index);
    when(webhook.delete()).thenReturn(delete);
    when(index.getAsLong()).thenReturn(0L);
    webhookEntity.setSubreddits(new ArrayList<>(List.of("redditdev")));
    
    command.run();
    
    // Has the database been updated?
    assertEquals(webhookEntity.getSubreddits(), Collections.emptyList());
    assertNull(webhookEntity.getChannel());
    assertNull(webhookEntity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(eq("redditdev"), any(Webhook.class));
    verify(delete).complete();
  }
  
  @Test
  public void testRemoveUnknownSubreddit() {
    // The webhook was created by a different user
    when(selfMember.getIdLong()).thenReturn(Long.MAX_VALUE);
    
    command = new RedditRemoveCommand(event, manager);
    command.run();
    
    verify(subredditObservable, times(0)).removeListener(anyString(), any(Webhook.class));
  }
  
  @Test
  public void testRemoveInvalidArguments() {
    when(event.getOption("name")).thenReturn(null);
    when(event.getOption("index")).thenReturn(null);
    
    command.run();
    
    verify(subredditObservable, times(0)).removeListener(anyString(), any(Webhook.class));
  }
  
  @Test
  public void testGetPermissions() {
    assertEquals(command.getPermissions(), EnumSet.of(Permission.MANAGE_CHANNEL));
  }
}
