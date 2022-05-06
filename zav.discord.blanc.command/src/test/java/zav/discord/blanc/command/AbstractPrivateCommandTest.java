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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static zav.test.io.JsonUtils.read;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.api.guice.PrivateCommandModule;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.UserTable;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Checks whether private commands fail if a user with insufficient rank tries to execute them.
 */
public class AbstractPrivateCommandTest {
  protected Injector injector;
  protected UserTable db;
  
  /**
   * Initializes the injector used for instantiating private commands.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    SlashCommandEvent event = mock(SlashCommandEvent.class);
    when(event.getJDA()).thenReturn(mock(JDA.class));
    when(event.getChannel()).thenReturn(mock(MessageChannel.class));
    when(event.getUser()).thenReturn(mock(User.class));
    when(event.getPrivateChannel()).thenReturn(mock(PrivateChannel.class));
    
    injector = Guice.createInjector(new PrivateCommandModule(event));
  
    db = injector.getInstance(UserTable.class);
  
    UserEntity user = read("User.json", UserEntity.class);
    db.put(user);
  }
  
  @AfterEach
  public void tearDown() throws IOException {
    Files.deleteIfExists(SqlQuery.ENTITY_DB_PATH);
    Files.deleteIfExists(SqlQuery.ENTITY_DB_PATH.getParent());
  }
  
  @Test
  public void testValidate() throws Exception {
    Command guildCommand = injector.getInstance(PrivateCommand.class);
    
    guildCommand.validate();
  }
  
  @Test
  public void testValidateWithDefaultRank() throws Exception {
    db = spy(db);
    when(db.get(anyLong())).thenThrow(new SQLException());
    
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
