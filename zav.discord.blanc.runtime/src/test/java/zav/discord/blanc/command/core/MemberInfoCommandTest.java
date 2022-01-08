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

package zav.discord.blanc.command.core;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.AbstractCommandTest;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.databind.UserDto;
import zav.discord.blanc.runtime.command.core.MemberInfoCommand;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class MemberInfoCommandTest extends AbstractCommandTest {
  private Command command;
  
  @Test
  public void testCommandIsOfCorrectType() {
    command = parse("b:member %s", selfUserId);
    
    assertThat(command).isInstanceOf(MemberInfoCommand.class);
  }
  
  @Test
  public void testSendMember() throws Exception {
    command = parse("b:member %s", selfUserId);
    
    command.run();
  
    // Provided user (user) should be picked
    ArgumentCaptor<UserDto> memberCaptor = ArgumentCaptor.forClass(UserDto.class);
    verify(channelView, times(1)).send(memberCaptor.capture());
    assertThat(memberCaptor.getValue()).isEqualTo(selfUserDto);
  }
  
  @Test
  public void testSendAuthor() throws Exception {
    command = parse("b:member");
  
    command.run();
  
    // Author (selfUser) should be picked
    ArgumentCaptor<UserDto> memberCaptor = ArgumentCaptor.forClass(UserDto.class);
    verify(channelView, times(1)).send(memberCaptor.capture());
    assertThat(memberCaptor.getValue()).isEqualTo(userDto);
  }
}
