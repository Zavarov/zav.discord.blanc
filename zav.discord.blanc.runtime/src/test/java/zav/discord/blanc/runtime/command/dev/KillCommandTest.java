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

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Client;

/**
 * Check whether all threads are terminated.
 */
@ExtendWith(MockitoExtension.class)
public class KillCommandTest {
  @Mock SlashCommandEvent event;
  @Mock ScheduledExecutorService queue;
  @Mock Client client;
  @Mock JDA jda;
  @Mock ReplyAction reply;
  KillCommand command;
  
  @Test
  public void testShutdown() {
    when(event.reply(anyString())).thenReturn(reply);
    when(reply.setEphemeral(anyBoolean())).thenReturn(reply);
    when(client.getShards()).thenReturn(List.of(jda));
    
    command = new KillCommand(event, queue, client);
    command.run();
    
    verify(jda, times(1)).shutdown();
    verify(queue, times(1)).shutdown();
  }
}
