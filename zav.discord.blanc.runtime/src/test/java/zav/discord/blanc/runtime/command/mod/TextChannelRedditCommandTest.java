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

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.runtime.command.AbstractGuildCommandTest;

/**
 * Checks whether subreddit feeds can be removed, but not added.
 */
public class TextChannelRedditCommandTest extends AbstractGuildCommandTest {
  private @Mock OptionMapping arg;
  
  @Test
  public void testAddSubreddit() throws Exception {
    when(guild.getIdLong()).thenReturn(guildEntity.getId());
    when(textChannel.getIdLong()).thenReturn(channelEntity.getId());
    when(textChannel.getGuild()).thenReturn(guild);
    when(event.getOption("subreddit")).thenReturn(arg);
    when(event.reply(anyString())).thenReturn(reply);
    when(arg.getAsString()).thenReturn("gamindustri");
    
    run(TextChannelRedditCommand.class);
  
    // The Reddit job shouldn't have been modified
    verify(subredditObservable, times(0)).addListener(anyString(), any(TextChannel.class));
  
    // The database shouldn't have been modified
    channelEntity = get(channelTable, textChannel);
    assertThat(channelEntity.getSubreddits()).containsExactly("redditdev", "announcements");
  }
  
  @Test
  public void testRemoveSubreddit() throws Exception {
    when(guild.getIdLong()).thenReturn(guildEntity.getId());
    when(textChannel.getIdLong()).thenReturn(channelEntity.getId());
    when(textChannel.getName()).thenReturn(channelEntity.getName());
    when(textChannel.getAsMention()).thenReturn(channelEntity.getName());
    when(textChannel.getGuild()).thenReturn(guild);
    when(event.getOption("subreddit")).thenReturn(arg);
    when(event.replyFormat(anyString(), anyString(), anyString())).thenReturn(reply);
    when(arg.getAsString()).thenReturn("RedditDev");
  
    run(TextChannelRedditCommand.class);
  
    // Has the Reddit job been updated?
    verify(subredditObservable, times(1)).removeListener(anyString(), any(TextChannel.class));
  
    // Has the database been updated?
    channelEntity = get(channelTable, textChannel);
    assertThat(channelEntity.getSubreddits()).containsExactly("announcements");
  }
}
