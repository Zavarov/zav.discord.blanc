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

package zav.discord.blanc.command.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.guice.PrivateCommandModule;
import zav.discord.blanc.command.InsufficientRankException;

/**
 * Checks whether private commands fail if a user with insufficient rank tries to execute them.
 */
public class AbstractPrivateCommandTest {
  Injector injector;
  
  /**
   * Initializes the injector used for instantiating private commands.
   */
  @BeforeEach
  public void setUp() {
    SlashCommandEvent event = mock(SlashCommandEvent.class);
    when(event.getJDA()).thenReturn(mock(JDA.class));
    when(event.getChannel()).thenReturn(mock(MessageChannel.class));
    when(event.getUser()).thenReturn(mock(User.class));
    when(event.getPrivateChannel()).thenReturn(mock(PrivateChannel.class));
    
    injector = Guice.createInjector(new PrivateCommandModule(event));
  }
  
  @Test
  public void testValidate() throws Exception {
    Command command = injector.getInstance(PrivateCommand.class);
    
    command.validate();
  }
  
  @Test
  public void testValidateWithInsufficientRank() {
    Command command = injector.getInstance(DeveloperCommand.class);
    
    assertThrows(InsufficientRankException.class, command::validate);
  }
}
