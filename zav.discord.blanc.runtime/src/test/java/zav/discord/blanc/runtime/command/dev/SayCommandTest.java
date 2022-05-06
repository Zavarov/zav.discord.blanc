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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import zav.discord.blanc.runtime.command.AbstractDevCommandTest;

/**
 * Check whether the command repeats the given argument.
 */
public class SayCommandTest extends AbstractDevCommandTest {
  private @Mock OptionMapping arg;
  
  @Test
  public void testSend() throws Exception {
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    when(event.reply(captor.capture())).thenReturn(reply);
    when(event.getOption(anyString())).thenReturn(arg);
    when(arg.getAsString()).thenReturn("Hello World");
    when(user.getIdLong()).thenReturn(userEntity.getId());
    
    run(SayCommand.class);
    
    assertThat(captor.getValue()).isEqualTo("Hello World");
  }
  
}
