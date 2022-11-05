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

package zav.discord.blanc.reddit;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.TextChannelEntity;

/**
 * Checks whether listeners are created for all valid registered text channels in the database.
 */
@Deprecated
@ExtendWith(MockitoExtension.class)
public class TextChannelInitializerTest {
  
  @Mock SubredditObservable observable;
  @Mock Guild guild;
  @Mock TextChannel textChannel1;
  @Mock TextChannel textChannel2;
  TextChannelEntity entity1;
  TextChannelEntity entity2;
  TextChannelInitializer initializer;
  MockedStatic<TextChannelEntity> mocked;
  
  /**
   * Creates a new instance of the text channel initializer and loads the database with a single
   * entity. The entity is registered to the subreddit {@code RedditDev}.
   */
  @BeforeEach
  public void setUp() {
    initializer = new TextChannelInitializer(observable);
    entity1 = new TextChannelEntity();
    entity1.setSubreddits(List.of("RedditDev"));
    entity2 = new TextChannelEntity();
    
    mocked = mockStatic(TextChannelEntity.class);
    mocked.when(() -> TextChannelEntity.find(textChannel1)).thenReturn(entity1);
    mocked.when(() -> TextChannelEntity.find(textChannel2)).thenReturn(entity2);
  }
  
  @AfterEach
  public void tearDown() {
    mocked.close();
  }
  
  @Test
  public void testLoad() {
    when(guild.getTextChannels()).thenReturn(List.of(textChannel1));
    
    initializer.load(guild);
    
    verify(observable).addListener("RedditDev", textChannel1);
    verify(observable, times(0)).addListener("RedditDev", textChannel2);
  }
  
  @Test
  public void testLoadUnrelatedTextChannels() {
    when(guild.getTextChannels()).thenReturn(List.of(textChannel2));
    
    initializer.load(guild);
    
    verify(observable, times(0)).addListener("RedditDev", textChannel1);
    verify(observable, times(0)).addListener("RedditDev", textChannel2);
  }
}
