package zav.discord.blanc.command.parser;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import zav.discord.blanc.Argument;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.AbstractPrivateCommand;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.command.Commands;
import zav.discord.blanc.databind.Message;
import zav.discord.blanc.view.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class ParserTest {
  private Parser parser;
  private GuildMessageView guildView;
  private PrivateMessageView privateView;
  private Message privateMessage;
  private Message guildMessage;
  
  @BeforeClass
  public static void setUpAll() {
    Commands.bind("guildCommand", GuildCommand::new);
    Commands.bind("privateCommand", PrivateCommand::new);
  }
  
  @AfterClass
  public static void tearDownAll() {
    Commands.clear();
  }
  
  @Before
  public void setUp() {
    // Mock private command
  
    privateMessage = mock(Message.class);
    
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
  
    guildMessage = mock(Message.class);
    
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
    
    when(parser.parse(privateMessage)).thenReturn(privateCommand);
    when(parser.parse(guildMessage)).thenReturn(guildCommand);
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
    when(parser.parse(privateMessage)).thenReturn(null);
    when(parser.parse(guildMessage)).thenReturn(null);
    
    Optional<? extends Command> result = parser.parse(guildView);
    assertThat(result).isEmpty();
    
    result = parser.parse(privateView);
    assertThat(result).isEmpty();
  }
  
  // -------------------------------------------------------------------------------------------- //
  //   Utilities                                                                                  //
  // -------------------------------------------------------------------------------------------- //
  
  private static class GuildCommand extends AbstractGuildCommand {
    public GuildCommand(List<? extends Argument> args){}
    @Override
    public void run() { }
  }
  
  private static class PrivateCommand extends AbstractPrivateCommand {
    public PrivateCommand(List<? extends Argument> args){}
    @Override
    public void run() { }
  }
}
