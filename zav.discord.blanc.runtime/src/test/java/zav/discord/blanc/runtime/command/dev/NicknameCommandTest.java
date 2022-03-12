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
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.runtime.command.AbstractDevCommandTest;

/**
 * Checks whether the nickname of the user account associated with the application can be modified.
 */
@ExtendWith(MockitoExtension.class)
public class NicknameCommandTest extends AbstractDevCommandTest {
  private @Mock AuditableRestAction<Void> action;
  
  @Test
  public void testCommandIsOfCorrectType() {
    check("b:dev.nickname", NicknameCommand.class);
  }
  
  @Test
  public void testSetNickname() throws Exception {
    when(guild.getSelfMember()).thenReturn(member);
    when(member.modifyNickname(anyString())).thenReturn(action);
    
    run("b:dev.nickname foo");
  
    ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
    verify(member, times(1)).modifyNickname(nameCaptor.capture());
    assertThat(nameCaptor.getValue()).isEqualTo("foo");
  
  }
  
  @Test
  public void testRemoveNickname() throws Exception {
    when(guild.getSelfMember()).thenReturn(member);
    when(member.modifyNickname(nullable(String.class))).thenReturn(action);
  
    run("b:dev.nickname");
  
    ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
    verify(member, times(1)).modifyNickname(nameCaptor.capture());
    assertThat(nameCaptor.getValue()).isNull();
  }
}
