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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.command.Command;
import zav.discord.blanc.api.command.IntermediateCommand;

/**
 * Checks whether the intermediate attributes of a command can be injected.
 */
public class IntermediateCommandModuleTest {
  Injector injector;
  
  /**
   * Initializes the injector over a fictitious intermediate command.
   */
  @BeforeEach
  public void setUp() {
    IntermediateCommand command = mock(IntermediateCommand.class);
    when(command.getArguments()).thenReturn(Collections.emptyList());
    when(command.getFlags()).thenReturn(Collections.emptyList());
    when(command.getName()).thenReturn(StringUtils.EMPTY);
    when(command.getPrefix()).thenReturn(Optional.empty());
    
    injector = Guice.createInjector(new IntermediateCommandModule(command));
  }
  
  @Test
  public void testCreateCommand() {
    injector.getInstance(TestCommand.class);
  }
  
  private static class TestCommand implements Command {
    @Inject
    @Named("args")
    private List<? extends Argument> args;
    
    @Inject
    @Named("flags")
    private List<String> flags;
    
    @Inject
    @Named("name")
    private String name;
    
    @Inject(optional = true)
    @Named("prefix")
    private String prefix;
    
    @Override
    public void run() {}
  
    @Override
    public void validate() {}
  }
}
