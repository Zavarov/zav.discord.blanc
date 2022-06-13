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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Check whether the command repeats the given argument.
 */
@ExtendWith(MockitoExtension.class)
public class SayCommandTest {
  @Mock OptionMapping content;
  @Mock SlashCommandEvent event;
  @Mock ReplyAction reply;
  SayCommand command;
  
  @Test
  public void testSend() {
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    when(event.reply(captor.capture())).thenReturn(reply);
    when(content.getAsString()).thenReturn("Hello World");
    
    command = new SayCommand(event, content);
    command.run();
    
    assertThat(captor.getValue()).isEqualTo("Hello World");
  }
  
}
