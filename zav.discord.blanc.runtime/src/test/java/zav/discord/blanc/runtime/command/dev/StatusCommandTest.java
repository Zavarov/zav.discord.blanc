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

package zav.discord.blanc.runtime.command.dev;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.Rank;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Check whether the bot status is properly displayed.
 */
@ExtendWith(MockitoExtension.class)
public class StatusCommandTest extends AbstractTest {

  StatusCommand command;
  
  @BeforeEach
  public void setUp() {
    command = new StatusCommand(event, manager);
  }
  
  @Test
  public void testSendStatus() {
    command.run();
    
    verify(event, times(1)).replyEmbeds(any(MessageEmbed.class));
  }
  
  @Test
  public void testGetRequiredRank() {
    assertEquals(command.getRequiredRank(), Rank.DEVELOPER);
  }
}
