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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.reddit.SubredditObservable;
import zav.discord.blanc.runtime.command.AbstractDatabaseTest;

/**
 * Checks whether subreddit feeds can be removed, but not added.
 */
@Deprecated
@ExtendWith(MockitoExtension.class)
public class RedditLegacyCommandTest extends AbstractDatabaseTest<TextChannelEntity> {
  @Mock SlashCommandEvent event;
  @Mock Guild guild;
  @Mock Member member;
  @Mock TextChannel channel;
  @Mock OptionMapping subreddit;
  @Mock RestAction<List<Webhook>> retrieveWebhooks;
  @Mock WebhookAction createWebhook;
  @Mock Webhook webhook;
  @Mock SubredditObservable observable;
  @Mock ReplyAction reply;
  GuildCommandManager manager;
  RedditLegacyCommand command;
  
  /**
   * Initializes the command with no arguments.
   */
  @BeforeEach
  public void setUp() {
    super.setUp(new TextChannelEntity());
    when(client.getSubredditObservable()).thenReturn(observable);
    when(event.getGuild()).thenReturn(guild);
    when(event.getMember()).thenReturn(member);
    when(event.getTextChannel()).thenReturn(channel);
    when(event.getOption(anyString())).thenReturn(subreddit);
    when(event.reply(anyString())).thenReturn(reply);
    when(subreddit.getAsString()).thenReturn("RedditDev");
    
    when(entityManager.find(eq(TextChannelEntity.class), any())).thenReturn(entity);
    when(entityManager.find(eq(GuildEntity.class), any())).thenReturn(new GuildEntity());
    
    manager = new GuildCommandManager(client, event);
    command = new RedditLegacyCommand(event, manager);
  }
  
  @Test
  public void testAddSubreddit() throws Exception {
    entity.setSubreddits(Lists.newArrayList());
    
    command.run();

    // Adding subreddits to text channels is deprecated. Hence the database shouldn't be modified.
    assertEquals(entity.getSubreddits(), Collections.emptyList());
    assertNull(entity.getGuild());
    // Adding subreddits to text channels is deprecated. Hence the Reddit job shouldn't be modified.
    verify(observable, times(0)).addListener(anyString(), any(TextChannel.class));
  }
  
  @Test
  public void testRemoveSubreddit() throws Exception {
    entity.setSubreddits(Lists.newArrayList("redditdev"));
    
    command.run();

    // Has the database been updated?
    assertEquals(entity.getSubreddits(), Collections.emptyList());
    assertNotNull(entity.getGuild());
    // Has the Reddit job been updated?
    verify(observable).removeListener(anyString(), any(TextChannel.class));
  }
}
