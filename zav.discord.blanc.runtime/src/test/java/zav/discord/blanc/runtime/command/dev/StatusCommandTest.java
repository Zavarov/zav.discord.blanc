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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.runtime.command.AbstractDevCommandTest;

/**
 * Check whether the bot status is properly displayed.
 */
public class StatusCommandTest extends AbstractDevCommandTest {
  
  @Test
  public void testSendStatus() throws Exception {
    when(user.getIdLong()).thenReturn(userEntity.getId());
    when(event.replyEmbeds(any(MessageEmbed.class))).thenReturn(reply);
    
    run(StatusCommand.class);
    
    verify(event, times(1)).replyEmbeds(any(MessageEmbed.class));
  }
}
