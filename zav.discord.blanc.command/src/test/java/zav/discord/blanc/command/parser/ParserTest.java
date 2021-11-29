package zav.discord.blanc.command.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.AbstractPrivateCommand;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.command.Commands;
import zav.discord.blanc.databind.MessageValueObject;
import zav.discord.blanc.view.GuildMessageView;
import zav.discord.blanc.view.GuildView;
import zav.discord.blanc.view.MemberView;
import zav.discord.blanc.view.PrivateChannelView;
import zav.discord.blanc.view.PrivateMessageView;
import zav.discord.blanc.view.ShardView;
import zav.discord.blanc.view.TextChannelView;
import zav.discord.blanc.view.UserView;

/**
 * Test case for the parser implementation.<br>
 * Verifies that the correct intermediate command representation is retrieved from a raw string.
 */
public class ParserTest {
  private Parser parser;
  private GuildMessageView guildView;
  private PrivateMessageView privateView;
  private MessageValueObject privateMessage;
  private MessageValueObject guildMessage;
  
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
  
    privateMessage = mock(MessageValueObject.class);
    
    IntermediateCommand privateCommand = mock(IntermediateCommand.class);
    when(privateCommand.getPrefix()).thenReturn(Optional.of("b"));
    when(privateCommand.getName()).thenReturn("privateCommand");
    when(privateCommand.getArguments()).thenReturn(Collections.emptyList());
    when(privateCommand.getFlags()).thenReturn(Collections.emptyList());
    
    privateView = mock(PrivateMessageView.class);
    when(privateView.getAuthor()).thenReturn(mock(UserView.class));
    when(privateView.getMessageChannel()).thenReturn(mock(PrivateChannelView.class));
    when(privateView.getShard()).thenReturn(mock(ShardView.class));
    when(privateView.getAbout()).thenReturn(privateMessage);
  
    // Mock guild command
  
    guildMessage = mock(MessageValueObject.class);
    
    IntermediateCommand guildCommand = mock(IntermediateCommand.class);
    when(guildCommand.getPrefix()).thenReturn(Optional.of("b"));
    when(guildCommand.getName()).thenReturn("guildCommand");
    when(guildCommand.getArguments()).thenReturn(Collections.emptyList());
    when(guildCommand.getFlags()).thenReturn(Collections.emptyList());
    
    guildView = mock(GuildMessageView.class);
    when(guildView.getAuthor()).thenReturn(mock(MemberView.class));
    when(guildView.getMessageChannel()).thenReturn(mock(TextChannelView.class));
    when(guildView.getGuild()).thenReturn(mock(GuildView.class));
    when(guildView.getShard()).thenReturn(mock(ShardView.class));
    when(guildView.getAbout()).thenReturn(guildMessage);
  
    // Mock parser
    
    parser = mock(AbstractParser.class);
    when(parser.parse(privateView)).thenCallRealMethod();
    when(parser.parse(guildView)).thenCallRealMethod();
    
    doReturn(privateCommand).when(parser).parse(privateMessage);
    doReturn(guildCommand).when(parser).parse(guildMessage);
  }
  
  @Test
  public void testParseGuildCommand() {
    Optional<? extends Command> result = parser.parse(guildView);
  
    assertThat(result).isPresent();
    assertThat(result.orElseThrow()).isInstanceOf(GuildCommand.class);
  }
  
  @Test
  public void testParsePrivateCommand() {
    Optional<? extends Command> result = parser.parse(privateView);
    
    assertThat(result).isPresent();
    assertThat(result.orElseThrow()).isInstanceOf(PrivateCommand.class);
  }
  
  @Test
  public void testParseInvalidCommand() {
    doReturn(null).when(parser).parse(privateMessage);
    doReturn(null).when(parser).parse(guildMessage);
    
    Optional<? extends Command> result = parser.parse(guildView);
    assertThat(result).isEmpty();
    
    result = parser.parse(privateView);
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
