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
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.api.command.Command;
import zav.discord.blanc.api.command.GuildCommandModule;
import zav.discord.blanc.api.command.IntermediateCommand;
import zav.discord.blanc.command.internal.IntermediateCommandModule;
import zav.discord.blanc.databind.UserDto;
import zav.discord.blanc.db.UserDatabase;

/**
 * Checks whether guild commands fail if a user with insufficient rank or permission tries to
 * execute them.
 */
public class AbstractGuildCommandTest {
  MockedStatic<UserDatabase> mocked;
  Injector injector;
  
  /**
   * Initializes the injector used for instantiating guild commands.
   */
  @BeforeEach
  public void setUp() {
    UserDto user = new UserDto().withRanks(List.of(Rank.USER.name()));
    
    Member member = mock(Member.class);
    when(member.getPermissions()).thenReturn(EnumSet.noneOf(Permission.class));
    
    mocked = mockStatic(UserDatabase.class);
    mocked.when(() -> UserDatabase.get(anyLong())).thenReturn(user);
  
    Message message = mock(Message.class);
    when(message.getJDA()).thenReturn(mock(JDA.class));
    when(message.getChannel()).thenReturn(mock(MessageChannel.class));
    when(message.getAuthor()).thenReturn(mock(User.class));
    when(message.getGuild()).thenReturn(mock(Guild.class));
    when(message.getTextChannel()).thenReturn(mock(TextChannel.class));
    when(message.getMember()).thenReturn(member);
  
    IntermediateCommand command = mock(IntermediateCommand.class);
    when(command.getArguments()).thenReturn(Collections.emptyList());
    when(command.getFlags()).thenReturn(Collections.emptyList());
    when(command.getName()).thenReturn(StringUtils.EMPTY);
    when(command.getPrefix()).thenReturn(Optional.empty());
    
    injector = Guice.createInjector(
          new GuildCommandModule(message),
          new IntermediateCommandModule(command)
    );
  }
  
  @AfterEach
  public void tearDown() {
    mocked.close();
  }
  
  @Test
  public void testValidate() throws Exception {
    Command guildCommand = injector.getInstance(GuildCommand.class);
    
    guildCommand.validate();
  }
  
  @Test
  public void testValidateWithInsufficientRank() {
    Command guildCommand = injector.getInstance(DeveloperCommand.class);
    
    assertThatThrownBy(guildCommand::validate)
          .isInstanceOf(InsufficientRankException.class);
  }
  
  @Test
  public void testValidateWithInsufficientPermission() {
    Command guildCommand = injector.getInstance(ModeratorCommand.class);
    
    assertThatThrownBy(guildCommand::validate)
          .isInstanceOf(InsufficientPermissionException.class);
  }
  
  private static class GuildCommand extends AbstractGuildCommand {
    @Override
    public void run() {}
  }
  
  private static class ModeratorCommand extends AbstractGuildCommand {
    
    public ModeratorCommand() {
      super(Permission.ADMINISTRATOR);
    }
    
    @Override
    public void run() {}
  }
  
  private static class DeveloperCommand extends AbstractGuildCommand {
    
    public DeveloperCommand() {
      super(Rank.ROOT);
    }
  
    @Override
    public void run() {}
  }
}
