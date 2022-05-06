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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.databind.WebHookEntity;
import zav.discord.blanc.runtime.command.AbstractGuildCommandTest;

/**
 * Check whether subreddits can be added and removed from the Reddit feed.
 */
public class WebhookRedditCommandTest extends AbstractGuildCommandTest {
  private @Mock RestAction<List<Webhook>> webhooks;
  private @Mock AuditableRestAction<Void> delete;
  private @Mock Webhook webhook;
  private @Mock OptionMapping arg;
  
  private long guildId;
  private long channelId;
  private long id;
  
  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    
    guildId = webhookEntity.getGuildId();
    channelId = webhookEntity.getChannelId();
    id = webhookEntity.getId();
  }
  
  @Test
  public void testAddSubreddit() throws Exception {
    when(webhooks.complete()).thenReturn(List.of(webhook));
    when(guild.getIdLong()).thenReturn(guildId);
    when(textChannel.getIdLong()).thenReturn(channelId);
    when(textChannel.getAsMention()).thenReturn(channelEntity.getName());
    when(textChannel.retrieveWebhooks()).thenReturn(webhooks);
    when(webhook.getGuild()).thenReturn(guild);
    when(webhook.getChannel()).thenReturn(textChannel);
    when(webhook.getIdLong()).thenReturn(id);
    when(webhook.getName()).thenReturn(webhookEntity.getName());
    when(event.getOption("subreddit")).thenReturn(arg);
    when(event.replyFormat(anyString(), anyString(), anyString())).thenReturn(reply);
    when(arg.getAsString()).thenReturn("gamindustri");
    
    run(WebhookRedditCommand.class);
  
    // Has the Reddit job been updated?
    verify(subredditObservable, times(1)).addListener(anyString(), any(Webhook.class));
  
    // Has the database been updated?
    webhookEntity = get(webhookTable, guildId, channelId, id);
    assertThat(webhookEntity.getSubreddits()).containsExactly("redditdev", "announcements", "gamindustri");
  }
  
  @Test
  public void testRemoveSubreddit() throws Exception {
    when(webhooks.complete()).thenReturn(List.of(webhook));
    when(guild.getIdLong()).thenReturn(guildEntity.getId());
    when(textChannel.getIdLong()).thenReturn(channelEntity.getId());
    when(textChannel.getAsMention()).thenReturn(channelEntity.getName());
    when(textChannel.retrieveWebhooks()).thenReturn(webhooks);
    when(webhook.getGuild()).thenReturn(guild);
    when(webhook.getChannel()).thenReturn(textChannel);
    when(webhook.getIdLong()).thenReturn(id);
    when(webhook.getName()).thenReturn(webhookEntity.getName());
    when(webhook.delete()).thenReturn(delete);
    when(event.getOption("subreddit")).thenReturn(arg);
    when(event.replyFormat(anyString(), anyString(), anyString())).thenReturn(reply);
    when(arg.getAsString()).thenReturn("redditdev");
  
    run(WebhookRedditCommand.class);
  
    // Has the Reddit job been updated?
    verify(subredditObservable, times(1)).removeListener(anyString(), any(Webhook.class));
  
    // Has the database been updated?
    WebHookEntity response = get(webhookTable, guildId, channelId, id);
    assertThat(response.getSubreddits()).containsExactly("announcements");
    when(arg.getAsString()).thenReturn("announcements");
  
    run(WebhookRedditCommand.class);
  
    // Has the Reddit job been updated?
    assertThat(subredditObservable.removeListener("Announcements", webhook)).isFalse();
  
    // Has the database been updated?
    response = get(webhookTable, guildId, channelId, id);
    assertThat(response.getSubreddits()).isEmpty();
    
    // Check that the webhook has been deleted when no more subreddits are registered.
    verify(webhook, times(1)).delete();
  }
}
