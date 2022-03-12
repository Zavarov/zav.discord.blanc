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

package zav.discord.blanc.command.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.Commands;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.AbstractPrivateCommand;
import zav.discord.blanc.command.IntermediateCommand;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Test case for the parser implementation.<br>
 * Verifies that the correct intermediate command representation is retrieved from a raw string.
 */
@ExtendWith(MockitoExtension.class)
public class AbstractParserTest {
  
  private @Mock GuildMessageReceivedEvent guildEvent;
  private @Mock PrivateMessageReceivedEvent privateEvent;
  private @Mock IntermediateCommand privateCommand;
  private @Mock IntermediateCommand guildCommand;
  private @Mock Message privateMessage;
  private @Mock Message guildMessage;
  private @Mock MessageChannel messageChannel;
  private @Mock MessageAction messageAction;
  private AbstractParser parser;
  
  /**
   * Initializes the command parser and commands.
   */
  @BeforeEach
  public void setUp() {
    Injector injector = Guice.createInjector();
    parser = new AbstractTestParser();
    injector.injectMembers(parser);
  
    Commands.bind("guildCommand", GuildCommand.class);
    Commands.bind("privateCommand", PrivateCommand.class);
  }
  
  /**
   * Deletes the database and clears all commands.
   *
   * @throws Exception If the database couldn't be deleted.
   */
  @AfterEach
  public void tearDown() throws Exception {
    Files.deleteIfExists(SqlQuery.ENTITY_DB_PATH);
    Files.deleteIfExists(SqlQuery.ENTITY_DB_PATH.getParent());
    Commands.clear();
  }
  
  @Test
  public void testParseHelpCommand() {
    when(guildCommand.getName()).thenReturn("guildCommand");
    when(guildCommand.getFlags()).thenReturn(List.of("h"));
    
    when(guildMessage.getChannel()).thenReturn(messageChannel);
    when(guildEvent.getMessage()).thenReturn(guildMessage);
    when(messageChannel.sendMessageEmbeds(any(MessageEmbed.class))).thenReturn(messageAction);
  
    assertThat(parser.parse(guildEvent)).isEmpty();
    verify(messageChannel, times(1)).sendMessageEmbeds(any(MessageEmbed.class));
  }
  
  @Test
  public void testParseGuildCommand() {
    when(guildCommand.getPrefix()).thenReturn(Optional.of("b"));
    when(guildCommand.getName()).thenReturn("guildCommand");
    when(guildCommand.getParameters()).thenReturn(Collections.emptyList());
    when(guildCommand.getFlags()).thenReturn(Collections.emptyList());
  
    when(guildMessage.getJDA()).thenReturn(mock(JDA.class));
    when(guildMessage.getChannel()).thenReturn(mock(MessageChannel.class));
    when(guildMessage.getAuthor()).thenReturn(mock(User.class));
    when(guildMessage.getGuild()).thenReturn(mock(Guild.class));
    when(guildMessage.getTextChannel()).thenReturn(mock(TextChannel.class));
    when(guildMessage.getMember()).thenReturn(mock(Member.class));
    
    when(guildEvent.getMessage()).thenReturn(guildMessage);
    
    Optional<? extends Command> result = parser.parse(guildEvent);
  
    assertThat(result).isPresent();
    assertThat(result.orElseThrow()).isInstanceOf(GuildCommand.class);
  }
  
  @Test
  public void testParsePrivateCommand() {
    when(privateCommand.getPrefix()).thenReturn(Optional.of("b"));
    when(privateCommand.getName()).thenReturn("privateCommand");
    when(privateCommand.getParameters()).thenReturn(Collections.emptyList());
    when(privateCommand.getFlags()).thenReturn(Collections.emptyList());
  
    when(privateMessage.getJDA()).thenReturn(mock(JDA.class));
    when(privateMessage.getChannel()).thenReturn(mock(MessageChannel.class));
    when(privateMessage.getAuthor()).thenReturn(mock(User.class));
    when(privateMessage.getPrivateChannel()).thenReturn(mock(PrivateChannel.class));
    
    when(privateEvent.getMessage()).thenReturn(privateMessage);
    
    Optional<? extends Command> result = parser.parse(privateEvent);
    
    assertThat(result).isPresent();
    assertThat(result.orElseThrow()).isInstanceOf(PrivateCommand.class);
  }
  
  @Test
  public void testParseInvalidCommand() {
    Optional<? extends Command> result;
    
    result = parser.parse(guildEvent);
    assertThat(result).isEmpty();
    
    result = parser.parse(privateEvent);
    assertThat(result).isEmpty();
    
    // Input has a valid format but no commands matching it exist
    Commands.clear();
  
    when(guildEvent.getMessage()).thenReturn(guildMessage);
    result = parser.parse(guildEvent);
    assertThat(result).isEmpty();
  
    when(privateEvent.getMessage()).thenReturn(privateMessage);
    result = parser.parse(privateEvent);
    assertThat(result).isEmpty();
  }
  
  // -------------------------------------------------------------------------------------------- //
  //   Utilities                                                                                  //
  // -------------------------------------------------------------------------------------------- //
  
  private static class GuildCommand extends AbstractGuildCommand {
    @Override
    public void run() { }
  }
  
  private static class PrivateCommand extends AbstractPrivateCommand {
    @Override
    public void run() { }
  }
  
  private class AbstractTestParser extends AbstractParser {
    @Override
    protected @Nullable IntermediateCommand parse(@NonNull Message source) {
      if (source == privateMessage) {
        return privateCommand;
      } else if (source == guildMessage) {
        return guildCommand;
      } else {
        return null;
      }
    }
  }
}
