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

package zav.discord.blanc.command.internal.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Command;

/**
 * Test case for resolving strings into member instances either by id, name or nickname.
 */
@ExtendWith(MockitoExtension.class)
public class MemberResolverTest extends AbstractResolverTest {
  
  @Test
  public void testInjectById() {
    when(message.getMember()).thenReturn(member);
    when(message.getGuild()).thenReturn(guild);
    when(parameter.asNumber()).thenReturn(Optional.of(BigDecimal.valueOf(number)));
    when(guild.getMemberById(number)).thenReturn(member);
    
    PrivateCommand cmd = injector.getInstance(PrivateCommand.class);
    
    assertThat(cmd.field).isEqualTo(member);
  }
  
  @Test
  public void testInjectByName() {
    when(message.getMember()).thenReturn(member);
    when(message.getGuild()).thenReturn(guild);
    when(parameter.asString()).thenReturn(Optional.of(string));
    when(guild.getMembersByName(eq(string), anyBoolean())).thenReturn(List.of(member));
  
    PrivateCommand cmd = injector.getInstance(PrivateCommand.class);
    
    assertThat(cmd.field).isEqualTo(member);
  }
  
  @Test
  public void testInjectByNickname() {
    when(message.getMember()).thenReturn(member);
    when(message.getGuild()).thenReturn(guild);
    when(parameter.asString()).thenReturn(Optional.of(string));
    when(guild.getMembersByNickname(eq(string), anyBoolean())).thenReturn(List.of(member));
    
    PrivateCommand cmd = injector.getInstance(PrivateCommand.class);
    
    assertThat(cmd.field).isEqualTo(member);
  }
  
  @Test
  public void testInjectDefault() {
    when(message.getMember()).thenReturn(member);
    when(message.getGuild()).thenReturn(guild);
    
    PrivateCommand cmd = injector.getInstance(PrivateCommand.class);
    
    assertThat(cmd.defaultField).isEqualTo(member);
  }
  
  @SuppressWarnings("unused")
  private static class PrivateCommand implements Command {
    @Argument(index = 0)
    private Member field;
    
    @Argument(index = 1, useDefault = true)
    private Member defaultField;
    
    @Override
    public void run() {}
    
    @Override
    public void validate() {}
  }
}
