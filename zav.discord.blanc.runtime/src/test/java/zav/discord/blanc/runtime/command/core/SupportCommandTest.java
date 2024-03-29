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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Checks whether the invitation link is contained in the message response.
 */
@ExtendWith(MockitoExtension.class)
public class SupportCommandTest extends AbstractTest {
  static final String supportServer = "https://discord.gg/xxxxxxxxxx";
  
  SupportCommand command;
  
  /**
   * Initializes the command with no arguments.
   */
  @BeforeEach
  public void setUp() {
    when(credentials.getInviteSupportServer()).thenReturn(supportServer);
    command = new SupportCommand(event, manager);
  }
  
  @Test
  public void testSendSupportLink() {
    when(event.reply(response.capture())).thenReturn(reply);
    
    command.run();
    
    assertTrue(response.getValue().endsWith(supportServer));
  }
}
