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
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
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
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.reddit.SubredditObservable;
import zav.discord.blanc.runtime.command.AbstractDatabaseTest;

/**
 * Check whether subreddits can be added and removed from the Reddit feed.
 */
@ExtendWith(MockitoExtension.class)
public class RedditCommandTest extends AbstractDatabaseTest<WebhookEntity> {
  @Mock AuditableRestAction<Void> delete;
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
  TextChannelEntity channelEntity;
  RedditCommand command;
  GuildEntity guildEntity;
  
  /**
   * Initializes the command with no arguments.
   */
  @BeforeEach
  public void setUp() {
    super.setUp(new WebhookEntity());
    guildEntity = new GuildEntity();
    channelEntity = new TextChannelEntity();

    when(client.getSubredditObservable()).thenReturn(observable);
    when(event.getGuild()).thenReturn(guild);
    when(event.getMember()).thenReturn(member);
    when(event.getTextChannel()).thenReturn(channel);
    when(event.getOption(anyString())).thenReturn(subreddit);
    when(event.reply(anyString())).thenReturn(reply);
    when(subreddit.getAsString()).thenReturn("RedditDev");
    
    when(channel.retrieveWebhooks()).thenReturn(retrieveWebhooks);
    when(channel.createWebhook(anyString())).thenReturn(createWebhook);
    when(createWebhook.complete()).thenReturn(webhook);
    when(retrieveWebhooks.complete()).thenReturn(List.of(webhook));
    
    when(entityManager.find(eq(WebhookEntity.class), any())).thenReturn(entity);
    when(entityManager.find(eq(GuildEntity.class), any())).thenReturn(guildEntity);
    when(entityManager.find(eq(TextChannelEntity.class), any())).thenReturn(channelEntity);

    manager = new GuildCommandManager(client, event);
    command = new RedditCommand(event, manager);
  }
  
  @Test
  public void testAddSubreddit() throws Exception {
    entity.setSubreddits(Lists.newArrayList());
    
    command.run();

    // Has the database been updated?
    assertEquals(entity.getSubreddits(), List.of("redditdev"));
    assertNotNull(entity.getChannel());
    assertNotNull(entity.getGuild());
    // Has the Reddit job been updated?
    verify(observable).addListener(anyString(), any(Webhook.class));
  }
  
  @Test
  public void testRemoveSubreddit() throws Exception {
    entity.setSubreddits(Lists.newArrayList("redditdev"));
    
    command.run();

    // Has the database been updated?
    assertEquals(entity.getSubreddits(), Collections.emptyList());
    assertNotNull(entity.getChannel());
    assertNotNull(entity.getGuild());
    // Has the Reddit job been updated?
    verify(observable).removeListener(anyString(), any(Webhook.class));
  }
  
  @Test
  public void testRemoveAndDeleteWebhook() throws Exception {
    when(webhook.delete()).thenReturn(delete);
    entity.setSubreddits(Lists.newArrayList("redditdev"));
    entity.setOwner(true);
    
    command.run();

    // Has the database been updated?
    assertEquals(entity.getSubreddits(), Collections.emptyList());
    assertNotNull(entity.getChannel());
    assertNotNull(entity.getGuild());
    // Has the Reddit job been updated?
    verify(observable).removeListener(anyString(), any(Webhook.class));
    // Has the webhook been deleted
    verify(delete).complete();
  }
  
  @Test
  public void testRemoveAndKeepWebhook() throws Exception {
    entity.setSubreddits(Lists.newArrayList("redditdev", "boiledgoulash"));
    entity.setOwner(true);
    
    command.run();

    // Has the database been updated?
    assertEquals(entity.getSubreddits(), Lists.newArrayList("boiledgoulash"));
    assertNotNull(entity.getChannel());
    assertNotNull(entity.getGuild());
    // Has the Reddit job been updated?
    verify(observable).removeListener(anyString(), any(Webhook.class));
    // Does the webhook still exists?
    verify(delete, times(0)).complete();
  }
}
