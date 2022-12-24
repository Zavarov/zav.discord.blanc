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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Tests whether the message containing the invitation shows all required information. Those being
 * the correct bot id, as well as a list of all permissions that are required by the bot.
 */
@ExtendWith(MockitoExtension.class)
public class InviteCommandTest extends AbstractTest {
  @Captor
  ArgumentCaptor<MessageEmbed> captor;
  InviteCommand command;
  
  @BeforeEach
  public void setUp() {
    when(event.replyEmbeds(captor.capture())).thenReturn(reply);
    command = new InviteCommand(event, manager);
  }
  
  @Test
  public void testRun() throws Exception {
    command.run();
    
    String description = captor.getValue().getDescription();
    
    assertTrue(description.contains(selfUser.getId()));
    assertTrue(description.contains("Read Messages"));
    assertTrue(description.contains("Send Messages"));
    assertTrue(description.contains("Manage Messages"));
    assertTrue(description.contains("Manage Webhooks"));
  }
}
