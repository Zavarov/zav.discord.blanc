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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static zav.test.io.JsonUtils.read;

import java.util.List;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.WebHookEntity;
import zav.discord.blanc.db.WebHookTable;
import zav.discord.blanc.reddit.SubredditObservable;
import zav.discord.blanc.runtime.command.AbstractCommandTest;

/**
 * Check whether subreddits can be added and removed from the Reddit feed.
 */
@ExtendWith(MockitoExtension.class)
public class RedditCommandTest extends AbstractCommandTest {
  private @Mock MessageAction action;
  private @Mock RestAction<List<Webhook>> webhooks;
  private @Mock AuditableRestAction<Void> delete;
  private @Mock Webhook webhook;
  
  private WebHookTable hookTable;
  private WebHookEntity hookEntity;
  private SubredditObservable observable;
  
  private long guildId;
  private long channelId;
  private long id;
  
  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
  
    observable = injector.getInstance(SubredditObservable.class);
    hookTable = injector.getInstance(WebHookTable.class);
    hookEntity = read("WebHook.json", WebHookEntity.class);
    
    guildId = hookEntity.getGuildId();
    channelId = hookEntity.getChannelId();
    id = hookEntity.getId();
    
    when(textChannel.getIdLong()).thenReturn(channelId);
    when(textChannel.retrieveWebhooks()).thenReturn(webhooks);
    when(webhooks.complete()).thenReturn(List.of(webhook));
    when(webhook.getGuild()).thenReturn(guild);
    when(webhook.getChannel()).thenReturn(textChannel);
    when(webhook.getIdLong()).thenReturn(id);
    when(webhook.getName()).thenReturn(hookEntity.getName());
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    when(webhook.getJDA()).thenReturn(shard);
    
    check("b:mod.reddit RedditDev", RedditCommand.class);
  }
  
  @Test
  public void testAddSubreddit() throws Exception {
    when(webhook.getJDA()).thenReturn(shard);
    when(guild.getIdLong()).thenReturn(guildId);
    when(textChannel.sendMessageFormat(any(), any(), any())).thenReturn(action);
    
    run("b:mod.reddit RedditDev", RedditCommand.class);
  
    // Has the Reddit job been updated?
    assertThat(observable.addListener("RedditDev", webhook)).isFalse();
  
    // Has the database been updated?
    WebHookEntity response = get(hookTable, guildId, channelId, id);
    assertThat(response.getSubreddits()).containsExactly("redditdev");
  }
  
  @Test
  public void testRemoveSubreddit() throws Exception {
    when(guild.getIdLong()).thenReturn(guildId);
    when(textChannel.sendMessageFormat(any(), any(), any())).thenReturn(action);
    when(webhook.delete()).thenReturn(delete);
    
    hookTable.put(hookEntity);
  
    run("b:mod.reddit RedditDev", RedditCommand.class);
  
    // Has the Reddit job been updated?
    assertThat(observable.removeListener("RedditDev", webhook)).isFalse();
  
    // Has the database been updated?
    WebHookEntity response = get(hookTable, guildId, channelId, id);
    assertThat(response.getSubreddits()).containsExactly("announcements");
  
    run("b:mod.reddit Announcements", RedditCommand.class);
  
    // Has the Reddit job been updated?
    assertThat(observable.removeListener("Announcements", webhook)).isFalse();
  
    // Has the database been updated?
    response = get(hookTable, guildId, channelId, id);
    assertThat(response.getSubreddits()).isEmpty();
    
    // Check that the webhook has been deleted when no more subreddits are registered.
    verify(webhook, times(1)).delete();
  }
}
