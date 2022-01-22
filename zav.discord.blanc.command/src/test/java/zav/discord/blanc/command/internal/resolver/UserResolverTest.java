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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Command;

public class UserResolverTest extends AbstractResolverTest {
  
  @Mock RestAction<User> action;
  
  @BeforeEach
  public void setUp() {
    super.setUp();
    when(jda.retrieveUserById(anyLong())).thenReturn(action);
    when(action.complete()).thenReturn(user);
  }
  
  @Test
  public void testInjectById() {
    PrivateCommand cmd = injector.getInstance(PrivateCommand.class);
    
    assertThat(cmd.field).isEqualTo(user);
  }
  
  @Test
  public void testInjectByName() {
    when(action.complete()).thenReturn(null);
    when(jda.getUsersByName(eq(string), anyBoolean())).thenReturn(List.of(user));
    
    PrivateCommand cmd = injector.getInstance(PrivateCommand.class);
    
    assertThat(cmd.field).isEqualTo(user);
  }
  
  @Test
  public void testInjectDefault() {
    when(action.complete()).thenReturn(null);
    
    PrivateCommand cmd = injector.getInstance(PrivateCommand.class);
    
    assertThat(cmd.defaultField).isEqualTo(user);
  }
  
  @SuppressWarnings("unused")
  private static class PrivateCommand implements Command {
    @Argument(index = 0)
    private User field;
    
    @Argument(index = 1, useDefault = true)
    private User defaultField;
    
    @Override
    public void run() {}
    
    @Override
    public void validate() {}
  }
}
