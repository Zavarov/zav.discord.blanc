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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
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
public class RedditAddCommandTest extends AbstractDatabaseTest<WebhookEntity> {
  @Mock OptionMapping name;
  GuildCommandManager manager;
  TextChannelEntity channelEntity;
  RedditAddCommand command;
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
    command = new RedditAddCommand(event, manager);
  }
  
  /**
   * Subreddits are case insensitive.
   *
   * @param subredditName A human-readable subreddit name.
   */
  @ParameterizedTest
  @ValueSource(strings = { "RedditDev", "redditDev", "ReDdItDeV", "redditdev" })
  public void testAddSubreddit(String subredditName) throws Exception {
    when(event.getOption(anyString())).thenReturn(name);
    when(name.getAsString()).thenReturn(subredditName);
    
    entity.setSubreddits(new ArrayList<>());
    
    command.run();

    // Has the database been updated?
    assertEquals(entity.getSubreddits(), List.of("redditdev"));
    assertNotNull(entity.getChannel());
    assertNotNull(entity.getGuild());
    // Has the Reddit job been updated?
    verify(subredditObservable).addListener(anyString(), any(Webhook.class));
  }
}