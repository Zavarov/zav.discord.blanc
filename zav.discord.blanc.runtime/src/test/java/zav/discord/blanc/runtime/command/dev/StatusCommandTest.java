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
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.command.CommandManager;

/**
 * Check whether the bot status is properly displayed.
 */
@ExtendWith(MockitoExtension.class)
public class StatusCommandTest {

  @Mock CommandManager manager;
  @Mock SlashCommandEvent event;
  @Mock ReplyAction reply;
  StatusCommand command;
  
  @BeforeEach
  public void setUp() {
    command = new StatusCommand(event, manager);
  }
  
  @Test
  public void testSendStatus() {
    when(event.replyEmbeds(any(MessageEmbed.class))).thenReturn(reply);
    
    command.run();
    
    verify(event, times(1)).replyEmbeds(any(MessageEmbed.class));
  }
}
