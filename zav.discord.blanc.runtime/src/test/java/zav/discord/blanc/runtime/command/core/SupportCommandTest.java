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

package zav.discord.blanc.runtime.command.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Checks whether the invitation link is contained in the message response.
 */
@ExtendWith(MockitoExtension.class)
public class SupportCommandTest {
  private static final String supportServer = "https://discord.gg/xxxxxxxxxx";
  
  @Mock SlashCommandEvent event;
  @Mock ReplyAction reply;
  SupportCommand command;
  
  @Test
  public void testSendSupportLink() {
    ArgumentCaptor<String> response = ArgumentCaptor.forClass(String.class);
  
    when(event.replyFormat(anyString(), response.capture())).thenReturn(reply);
    
    command = new SupportCommand(event, supportServer);
    command.run();
    
    assertThat(response.getValue()).isEqualTo(supportServer);
  }
}
