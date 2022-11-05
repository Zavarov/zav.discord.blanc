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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
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
 * Checks whether subreddit feeds can be removed, but not added.
 */
@Deprecated
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LegacyRedditRemoveCommandTest extends AbstractTest {
  @Mock OptionMapping name;
  @Mock OptionMapping index;
  
  GuildCommandManager manager;
  LegacyRedditRemoveCommand command;
  
  /**
   * Initializes the command with no arguments.
   */
  @BeforeEach
  public void setUp() {
    channelEntity.setSubreddits(new ArrayList<>(List.of("RedditDev")));
    channelEntity.setWebhooks(Collections.emptyList());

    manager = new GuildCommandManager(client, event);
    command = new LegacyRedditRemoveCommand(event, manager);
  }
  
  /**
   * Subreddits are case insensitive.
   *
   * @param subredditName A human-readable subreddit name.
   */
  @ParameterizedTest
  @ValueSource(strings = { "RedditDev", "redditDev", "ReDdItDeV", "redditdev" })
  public void testRemoveLastSubredditByName(String subredditName) {
    when(event.getOption("name")).thenReturn(name);
    when(event.getOption("index")).thenReturn(null);
    when(name.getAsString()).thenReturn(subredditName);

    channelEntity.setSubreddits(new ArrayList<>(List.of("redditdev")));
    
    command.run();

    // Has the database been updated?
    assertEquals(channelEntity.getSubreddits(), Collections.emptyList());
    assertNull(channelEntity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(eq("redditdev"), any(TextChannel.class));
  }
  
  @Test
  public void testRemoveLastSubredditByIndex() throws Exception {
    when(event.getOption("name")).thenReturn(null);
    when(event.getOption("index")).thenReturn(index);
    when(index.getAsLong()).thenReturn(0L);
    
    channelEntity.setSubreddits(new ArrayList<>(List.of("redditdev")));
    
    command.run();

    // Has the database been updated?
    assertEquals(channelEntity.getSubreddits(), Collections.emptyList());
    assertNull(channelEntity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(eq("redditdev"), any(TextChannel.class));
  }
  
  @Test
  public void testRemoveSubredditByName() {
    when(event.getOption("name")).thenReturn(name);
    when(event.getOption("index")).thenReturn(null);
    when(name.getAsString()).thenReturn("redditdev");

    channelEntity.setSubreddits(new ArrayList<>(List.of("redditdev", "all")));
    
    command.run();

    // Has the database been updated?
    assertEquals(channelEntity.getSubreddits(), Collections.singletonList("all"));
    assertNotNull(channelEntity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(eq("redditdev"), any(TextChannel.class));
  }
  
  @Test
  public void testRemoveSubredditByIndex() throws Exception {
    when(event.getOption("name")).thenReturn(null);
    when(event.getOption("index")).thenReturn(index);
    when(index.getAsLong()).thenReturn(0L);
    
    channelEntity.setSubreddits(new ArrayList<>(List.of("redditdev", "all")));
    
    command.run();

    // Has the database been updated?
    assertEquals(channelEntity.getSubreddits(), Collections.singletonList("all"));
    assertNotNull(channelEntity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).removeListener(eq("redditdev"), any(TextChannel.class));
  }
  
  @Test
  public void testGetPermissions() {
    assertEquals(command.getPermissions(), EnumSet.of(Permission.MANAGE_CHANNEL));
  }
}
