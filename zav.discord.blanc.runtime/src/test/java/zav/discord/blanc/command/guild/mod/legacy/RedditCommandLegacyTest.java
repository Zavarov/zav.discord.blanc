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

package zav.discord.blanc.command.guild.mod.legacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.AbstractCommandTest;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.databind.TextChannel;
import zav.discord.blanc.databind.WebHook;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.db.WebHookTable;
import zav.discord.blanc.runtime.command.guild.mod.RedditCommand;
import zav.discord.blanc.runtime.command.guild.mod.legacy.RedditCommandLegacy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class RedditCommandLegacyTest  extends AbstractCommandTest {
  private Command command;
  
  @BeforeEach
  public void setUp() {
    command = parse("b:mod.legacy.reddit %s %s", channelSubreddit, channelId);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(RedditCommandLegacy.class);
  }
  
  @Test
  public void testAddSubreddit() throws Exception {
    channel.getSubreddits().remove(channelSubreddit);
    
    command.run();
  
    // Has the subreddit been added?
    assertThat(channel.getSubreddits()).isEmpty();
  
    // Correct message?
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    verify(channelView, times(1)).send(msgCaptor.capture());
  
    assertThat(msgCaptor.getValue()).isEqualTo("This functionality is deprecated. Please use the `reddit` command instead.");
  }
  
  @Test
  public void testRemoveSubreddit() throws Exception {
    command.run();
  
    // Correct message?
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> subredditCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> channelCaptor = ArgumentCaptor.forClass(String.class);
    verify(channelView, times(1)).send(msgCaptor.capture(), subredditCaptor.capture(), channelCaptor.capture());
  
    assertThat(msgCaptor.getValue()).isEqualTo("Submissions from r/%s will no longer be posted in %s.");
    assertThat(subredditCaptor.getValue()).isEqualTo(channelSubreddit);
    assertThat(channelCaptor.getValue()).isEqualTo(channelName);
  
    verify(channelView, times(1)).updateSubreddit(subredditCaptor.getValue());
    assertThat(subredditCaptor.getValue()).isEqualTo(channelSubreddit);
  
    TextChannel dbChannel = TextChannelTable.get(guildId, channelId);
    assertThat(dbChannel.getId()).isEqualTo(channelId);
    assertThat(dbChannel.getName()).isEqualTo(channelName);
    assertThat(dbChannel.getSubreddits()).isEmpty();
  }
}
