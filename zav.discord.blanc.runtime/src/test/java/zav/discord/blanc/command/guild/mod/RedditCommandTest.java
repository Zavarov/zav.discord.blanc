/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.command.guild.mod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.AbstractCommandTest;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.databind.WebHook;
import zav.discord.blanc.db.WebHookTable;
import zav.discord.blanc.runtime.command.guild.mod.RedditCommand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class RedditCommandTest extends AbstractCommandTest {
  private Command command;
  
  @BeforeEach
  public void setUp() {
    command = parse("b:mod.reddit %s %s", webHookSubreddit, channelId);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(RedditCommand.class);
  }
  
  @Test
  public void testAddSubreddit() throws Exception {
    webHook.getSubreddits().remove(webHookSubreddit);
    when(channelView.updateSubreddit(anyString())).thenReturn(true);
    
    command.run();
    
    // Has the subreddit been added?
    assertThat(webHook.getSubreddits()).contains(webHookSubreddit);
    
    // Correct message?
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> subredditCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> channelCaptor = ArgumentCaptor.forClass(String.class);
    verify(channelView, times(1)).send(msgCaptor.capture(), subredditCaptor.capture(), channelCaptor.capture());
    
    assertThat(msgCaptor.getValue()).isEqualTo("Submissions from r/%s will be posted in %s.");
    assertThat(subredditCaptor.getValue()).isEqualTo(webHookSubreddit);
    assertThat(channelCaptor.getValue()).isEqualTo(channelName);
    
    verify(channelView, times(1)).updateSubreddit(subredditCaptor.getValue());
    assertThat(subredditCaptor.getValue()).isEqualTo(webHookSubreddit);
  
    // Has the database been updated?
    WebHook dbHook = WebHookTable.get(guildId, channelId, webHookId);
    assertThat(dbHook.getId()).isEqualTo(webHookId);
    assertThat(dbHook.getName()).isEqualTo(webHookName);
    assertThat(dbHook.getChannelId()).isEqualTo(channelId); // channelId was given as an argument
    assertThat(dbHook.isOwner()).isEqualTo(webHookOwner);
    assertThat(dbHook.getSubreddits()).containsExactly(webHookSubreddit);
  }
  
  @Test
  public void testRemoveSubreddit() throws Exception {
    when(channelView.updateSubreddit(anyString())).thenReturn(false);
  
    command.run();
  
    // Has the subreddit been added?
    assertThat(webHook.getSubreddits()).isEmpty();
  
    // Correct message?
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> subredditCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> channelCaptor = ArgumentCaptor.forClass(String.class);
    verify(channelView, times(1)).send(msgCaptor.capture(), subredditCaptor.capture(), channelCaptor.capture());
  
    assertThat(msgCaptor.getValue()).isEqualTo("Submissions from r/%s will no longer be posted in %s.");
    assertThat(subredditCaptor.getValue()).isEqualTo(webHookSubreddit);
    assertThat(channelCaptor.getValue()).isEqualTo(channelName);
  
    verify(channelView, times(1)).updateSubreddit(subredditCaptor.getValue());
    assertThat(subredditCaptor.getValue()).isEqualTo(webHookSubreddit);
  
    // Has the database been updated?
    WebHook dbHook = WebHookTable.get(guildId, channelId, webHookId);
    assertThat(dbHook.getId()).isEqualTo(webHookId);
    assertThat(dbHook.getName()).isEqualTo(webHookName);
    assertThat(dbHook.getChannelId()).isEqualTo(channelId); // channelId was given as an argument
    assertThat(dbHook.isOwner()).isEqualTo(webHookOwner);
    assertThat(dbHook.getSubreddits()).isEmpty();
  }
}
