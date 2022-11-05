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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * This test case verifies whether the list of all currently registered Reddit feeds is correctly
 * displayed.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LegacyRedditInfoCommandTest extends AbstractTest {
  @Captor ArgumentCaptor<List<Site.Page>> pages;
  
  GuildCommandManager manager;
  LegacyRedditInfoCommand command;
  
  /**
   * Initializes the command with no arguments. The database is initialized with one webhook and one
   * text channel entity, both registered to the subreddit {@code RedditDev}.
   */
  @BeforeEach
  public void setUp() {
    manager = spy(new GuildCommandManager(client, event));
    command = new LegacyRedditInfoCommand(event, manager);
    channelEntity.setSubreddits(new ArrayList<>(List.of("RedditDev", "BoatsOnWheels")));
    
    doNothing().when(manager).submit(pages.capture());
  }
  
  @Test
  public void testShowEmptyPage() throws Exception {
    guildEntity.setTextChannels(new ArrayList<>());
    
    command.run();
  
    assertTrue(pages.getValue().isEmpty());
  }
  
  @Test
  public void testShowPage() throws Exception {
    guildEntity.setTextChannels(new ArrayList<>(List.of(channelEntity)));

    command.run();
  
    assertEquals(pages.getValue().size(), 1);
  }
  
  @Test
  public void testGetPermissions() {
    assertEquals(command.getPermissions(), EnumSet.of(Permission.MANAGE_CHANNEL));
  }
}
