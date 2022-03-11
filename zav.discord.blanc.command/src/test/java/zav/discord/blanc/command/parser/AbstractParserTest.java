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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.Collections;
import java.util.Optional;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.Commands;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.AbstractPrivateCommand;
import zav.discord.blanc.command.IntermediateCommand;
import zav.discord.blanc.command.parser.AbstractParser;

/**
 * Test case for the parser implementation.<br>
 * Verifies that the correct intermediate command representation is retrieved from a raw string.
 */
public class AbstractParserTest {
  
  private @Mock GuildMessageReceivedEvent guildEvent;
  private @Mock PrivateMessageReceivedEvent privateEvent;
  private @Mock IntermediateCommand privateCommand;
  private @Mock IntermediateCommand guildCommand;
  private @Mock Message privateMessage;
  private @Mock Message guildMessage;
  private @Mock AbstractParser parser;
  private AutoCloseable closeable;
  
  @BeforeAll
  public static void setUpAll() {
    Commands.bind("guildCommand", GuildCommand.class);
    Commands.bind("privateCommand", PrivateCommand.class);
  }
  
  @AfterAll
  public static void tearDownAll() {
    Commands.clear();
  }
  
  /**
   * Initializes the resolver. Furthermore, instances of guild and private messages as well as
   * their corresponding commands are created.
   */
  @BeforeEach
  public void setUp() {
    closeable = openMocks(this);
    // Mock private command
    
    when(privateCommand.getPrefix()).thenReturn(Optional.of("b"));
    when(privateCommand.getName()).thenReturn("privateCommand");
    when(privateCommand.getParameters()).thenReturn(Collections.emptyList());
    when(privateCommand.getFlags()).thenReturn(Collections.emptyList());
    
    when(privateMessage.getJDA()).thenReturn(mock(JDA.class));
    when(privateMessage.getChannel()).thenReturn(mock(MessageChannel.class));
    when(privateMessage.getAuthor()).thenReturn(mock(User.class));
    when(privateMessage.getPrivateChannel()).thenReturn(mock(PrivateChannel.class));
  
    // Mock guild command
    
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
    
    when(parser.parse(any(PrivateMessageReceivedEvent.class))).thenCallRealMethod();
    when(parser.parse(any(GuildMessageReceivedEvent.class))).thenCallRealMethod();
    
    // Mock events
    
    when(guildEvent.getMessage()).thenReturn(guildMessage);
    when(privateEvent.getMessage()).thenReturn(privateMessage);
  
    // Inject injector into the parser
    Injector injector = Guice.createInjector();
    injector.injectMembers(parser);
  
    doReturn(privateCommand).when(parser).parse(privateMessage);
    doReturn(guildCommand).when(parser).parse(guildMessage);
  }
  
  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }
  
  @Test
  public void testParseGuildCommand() {
    Optional<? extends Command> result = parser.parse(guildEvent);
  
    assertThat(result).isPresent();
    assertThat(result.orElseThrow()).isInstanceOf(GuildCommand.class);
  }
  
  @Test
  public void testParsePrivateCommand() {
    Optional<? extends Command> result = parser.parse(privateEvent);
    
    assertThat(result).isPresent();
    assertThat(result.orElseThrow()).isInstanceOf(PrivateCommand.class);
  }
  
  @Test
  public void testParseInvalidCommand() {
    doReturn(null).when(parser).parse(privateMessage);
    doReturn(null).when(parser).parse(guildMessage);
    
    Optional<? extends Command> result;
    
    result = parser.parse(guildEvent);
    assertThat(result).isEmpty();
    
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
}
