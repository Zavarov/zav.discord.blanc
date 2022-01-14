package zav.discord.blanc.command.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.api.command.Command;
import zav.discord.blanc.api.command.Commands;
import zav.discord.blanc.api.command.GuildCommandModule;
import zav.discord.blanc.api.command.IntermediateCommand;
import zav.discord.blanc.api.command.PrivateCommandModule;
import zav.discord.blanc.api.command.parser.Parser;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.AbstractPrivateCommand;

/**
 * Test case for the parser implementation.<br>
 * Verifies that the correct intermediate command representation is retrieved from a raw string.
 */
public class AbstractParserTest {
  private Parser parser;

  private Message guildMessage;
  private Message privateMessage;

  private Module guildModule;
  private Module privateModule;
  
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
    // Mock private command
    
    IntermediateCommand privateCommand = mock(IntermediateCommand.class);
    when(privateCommand.getPrefix()).thenReturn(Optional.of("b"));
    when(privateCommand.getName()).thenReturn("privateCommand");
    when(privateCommand.getArguments()).thenReturn(Collections.emptyList());
    when(privateCommand.getFlags()).thenReturn(Collections.emptyList());
    
    privateMessage = mock(Message.class);
    when(privateMessage.getJDA()).thenReturn(mock(JDA.class));
    when(privateMessage.getChannel()).thenReturn(mock(MessageChannel.class));
    when(privateMessage.getAuthor()).thenReturn(mock(User.class));
    when(privateMessage.getPrivateChannel()).thenReturn(mock(PrivateChannel.class));
    
    privateModule = new PrivateCommandModule(privateMessage);
  
    // Mock guild command
    
    IntermediateCommand guildCommand = mock(IntermediateCommand.class);
    when(guildCommand.getPrefix()).thenReturn(Optional.of("b"));
    when(guildCommand.getName()).thenReturn("guildCommand");
    when(guildCommand.getArguments()).thenReturn(Collections.emptyList());
    when(guildCommand.getFlags()).thenReturn(Collections.emptyList());
  
    guildMessage = mock(Message.class);
    when(guildMessage.getJDA()).thenReturn(mock(JDA.class));
    when(guildMessage.getChannel()).thenReturn(mock(MessageChannel.class));
    when(guildMessage.getAuthor()).thenReturn(mock(User.class));
    when(guildMessage.getGuild()).thenReturn(mock(Guild.class));
    when(guildMessage.getTextChannel()).thenReturn(mock(TextChannel.class));
    when(guildMessage.getMember()).thenReturn(mock(Member.class));
    
    guildModule = new GuildCommandModule(guildMessage);
    
    parser = mock(AbstractParser.class);
    when(parser.parse(any(), eq(privateMessage))).thenCallRealMethod();
    when(parser.parse(any(), eq(guildMessage))).thenCallRealMethod();
  
    // Inject injector into the parser
    Injector injector = Guice.createInjector();
    injector.injectMembers(parser);
  
    doReturn(privateCommand).when(parser).parse(privateMessage);
    doReturn(guildCommand).when(parser).parse(guildMessage);
  }
  
  @Test
  public void testParseGuildCommand() {
    Optional<? extends Command> result = parser.parse(guildModule, guildMessage);
  
    assertThat(result).isPresent();
    assertThat(result.orElseThrow()).isInstanceOf(GuildCommand.class);
  }
  
  @Test
  public void testParsePrivateCommand() {
    Optional<? extends Command> result = parser.parse(privateModule, privateMessage);
    
    assertThat(result).isPresent();
    assertThat(result.orElseThrow()).isInstanceOf(PrivateCommand.class);
  }
  
  @Test
  public void testParseInvalidCommand() {
    doReturn(null).when(parser).parse(privateMessage);
    doReturn(null).when(parser).parse(guildMessage);
    
    Optional<? extends Command> result;
    
    result = parser.parse(mock(Module.class), guildMessage);
    assertThat(result).isEmpty();
    
    result = parser.parse(mock(Module.class), privateMessage);
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
