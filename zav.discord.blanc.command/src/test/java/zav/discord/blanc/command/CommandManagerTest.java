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

package zav.discord.blanc.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.command.internal.RankValidator;
import zav.discord.blanc.databind.Rank;

/**
 * This test case checks whether the utility functions provided by the command manager are
 * functional.
 */
@ExtendWith(MockitoExtension.class)
public class CommandManagerTest {
  @Mock InsufficientRankException exception;
  @Mock Shard shard;
  @Mock SlashCommandEvent event;
  CommandManager manager;
  
  /**
   * Initializes the CommandManager instance with a mocked rank validator.
   */
  @BeforeEach
  public void setUp() {
    try (var mocked = mockConstruction(RankValidator.class)) {
      manager = new CommandManager(shard, event);
    }
  }
  
  @Test
  public void testValidate() throws InsufficientRankException {
    // RankValidator has been mocked, hence the call should proceed without any error
    manager.validate(Rank.ROOT);
  }
  
  @Test
  public void testGetShard() {
    assertEquals(manager.getShard(), shard);
  }
}
