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

package zav.discord.blanc.runtime.command.mod.legacy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static zav.test.io.JsonUtils.read;

import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.reddit.SubredditObservable;
import zav.discord.blanc.runtime.command.AbstractCommandTest;

/**
 * Checks whether subreddit feeds can be removed, but not added.
 */
@ExtendWith(MockitoExtension.class)
public class RedditCommandLegacyTest extends AbstractCommandTest {
  private @Mock MessageAction action;
  
  private TextChannelTable channelTable;
  private TextChannelEntity channelEntity;
  private SubredditObservable observable;
  
  private long guildId;
  private long id;
  
  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
  
    observable = injector.getInstance(SubredditObservable.class);
    channelTable = injector.getInstance(TextChannelTable.class);
    channelEntity = read("TextChannel.json", TextChannelEntity.class);
    
    guildId = channelEntity.getGuildId();
    id = channelEntity.getId();
    
    when(textChannel.getGuild()).thenReturn(guild);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    check("b:mod.legacy.reddit RedditDev", RedditCommandLegacy.class);
  }
  
  @Test
  public void testAddSubreddit() throws Exception {
    when(guild.getTextChannelById(anyLong())).thenReturn(textChannel);
    when(guild.getIdLong()).thenReturn(guildId);
    when(textChannel.sendMessage(anyString())).thenReturn(action);
    when(textChannel.getIdLong()).thenReturn(id);
    
    run("b:mod.legacy.reddit %s %s", "RedditDev", id);
  
    // The Reddit job shouldn't have been modified
    assertThat(observable.addListener("RedditDev", textChannel)).isTrue();
  
    // The database shouldn't have been modified
    assertThat(channelTable.contains(guildId, id)).isFalse();
  }
  
  @Test
  public void testRemoveSubreddit() throws Exception {
    when(guild.getTextChannelById(anyLong())).thenReturn(textChannel);
    when(guild.getIdLong()).thenReturn(guildId);
    when(textChannel.sendMessageFormat(any(), any(), any())).thenReturn(action);
    when(textChannel.getIdLong()).thenReturn(id);
    when(textChannel.getName()).thenReturn(channelEntity.getName());
    
    channelTable.put(channelEntity);
    
    run("b:mod.legacy.reddit %s %s", "RedditDev", id);
  
    // Has the Reddit job been updated?
    assertThat(observable.removeListener("RedditDev", textChannel)).isFalse();
  
    // Has the database been updated?
    TextChannelEntity response = get(channelTable, guildId, id);
    assertThat(response.getSubreddits()).containsExactly("announcements");
  }
}
