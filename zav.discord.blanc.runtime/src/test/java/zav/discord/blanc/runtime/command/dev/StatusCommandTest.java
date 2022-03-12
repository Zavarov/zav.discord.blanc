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
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.runtime.command.AbstractDevCommandTest;

/**
 * Check whether the bot status is properly displayed.
 */
@ExtendWith(MockitoExtension.class)
public class StatusCommandTest extends AbstractDevCommandTest {
  private @Mock MessageAction action;
  
  @Test
  public void testCommandIsOfCorrectType() {
    check("b:dev.status", StatusCommand.class);
  }
  
  @Test
  public void testSend() throws Exception {
    when(textChannel.sendMessageEmbeds(any(MessageEmbed.class))).thenReturn(action);
    
    run("b:dev.status");
    
    ArgumentCaptor<MessageEmbed> msgCaptor = ArgumentCaptor.forClass(MessageEmbed.class);
    verify(textChannel, times(1)).sendMessageEmbeds(msgCaptor.capture());
  }
}
