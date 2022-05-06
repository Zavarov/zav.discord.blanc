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

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.runtime.command.AbstractCommandTest;

/**
 * Checks whether the invitation link is contained in the message response.
 */
public class SupportCommandTest extends AbstractCommandTest {
  
  @Test
  public void testSendSupportLink() throws Exception {
    ArgumentCaptor<String> response = ArgumentCaptor.forClass(String.class);
  
    when(event.replyFormat(anyString(), response.capture())).thenReturn(reply);
    
    run(SupportCommand.class);
    
    assertThat(response.getValue()).isEqualTo(SUPPORT_SERVER);
  }
}
