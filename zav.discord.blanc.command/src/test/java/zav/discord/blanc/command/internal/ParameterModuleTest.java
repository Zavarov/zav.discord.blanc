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

package zav.discord.blanc.command.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.command.internal.resolver.AbstractResolverTest;

/**
 * Check whether unknown types are resolved as {@code null}.
 */
@ExtendWith(MockitoExtension.class)
public class ParameterModuleTest extends AbstractResolverTest {
  
  @Test
  public void testInject() {
    PrivateCommand cmd = injector.getInstance(PrivateCommand.class);
    
    assertThat(cmd.field).isNull();
  }
  
  private static class PrivateCommand implements Command {
    @Argument(index = 0)
    @SuppressWarnings("unused")
    private Object field;
    
    @Override
    public void run() {}
    
    @Override
    public void validate() {}
  }
}
