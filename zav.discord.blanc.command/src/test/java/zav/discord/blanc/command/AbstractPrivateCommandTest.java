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

package zav.discord.blanc.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.api.command.Command;
import zav.discord.blanc.api.command.IntermediateCommand;
import zav.discord.blanc.api.command.PrivateCommandModule;
import zav.discord.blanc.command.internal.IntermediateCommandModule;
import zav.discord.blanc.databind.UserDto;
import zav.discord.blanc.db.UserDatabase;

/**
 * Checks whether private commands fail if a user with insufficient rank tries to execute them.
 */
public class AbstractPrivateCommandTest {
  MockedStatic<UserDatabase> mocked;
  Injector injector;
  
  /**
   * Initializes the injector used for instantiating private commands.
   */
  @BeforeEach
  public void setUp() {
    UserDto user = new UserDto().withRanks(List.of(Rank.USER.name()));
    
    mocked = mockStatic(UserDatabase.class);
    mocked.when(() -> UserDatabase.get(anyLong())).thenReturn(user);
    
    Message message = mock(Message.class);
    when(message.getJDA()).thenReturn(mock(JDA.class));
    when(message.getChannel()).thenReturn(mock(MessageChannel.class));
    when(message.getAuthor()).thenReturn(mock(User.class));
    when(message.getPrivateChannel()).thenReturn(mock(PrivateChannel.class));
    
    IntermediateCommand command = mock(IntermediateCommand.class);
    when(command.getParameters()).thenReturn(Collections.emptyList());
    when(command.getFlags()).thenReturn(Collections.emptyList());
    when(command.getName()).thenReturn(StringUtils.EMPTY);
    when(command.getPrefix()).thenReturn(Optional.empty());
    
    injector = Guice.createInjector(
          new PrivateCommandModule(message),
          new IntermediateCommandModule(command)
    );
  }
  
  @AfterEach
  public void tearDown() {
    mocked.close();
  }
  
  @Test
  public void testValidate() throws Exception {
    Command guildCommand = injector.getInstance(PrivateCommand.class);
    
    guildCommand.validate();
  }
  
  @Test
  public void testValidateWithDefaultRank() throws Exception {
    mocked.when(() -> UserDatabase.get(anyLong())).thenThrow(new SQLException());
    
    Command guildCommand = injector.getInstance(PrivateCommand.class);
    
    guildCommand.validate();
  }
  
  @Test
  public void testValidateWithInsufficientRank() {
    Command guildCommand = injector.getInstance(DeveloperCommand.class);
    
    assertThatThrownBy(guildCommand::validate)
          .isInstanceOf(InsufficientRankException.class);
  }
  
  private static class PrivateCommand extends AbstractPrivateCommand {
    @Override
    public void run() {}
  }
  
  private static class DeveloperCommand extends AbstractPrivateCommand {
    
    public DeveloperCommand() {
      super(Rank.ROOT);
    }
    
    @Override
    public void run() {}
  }
}
