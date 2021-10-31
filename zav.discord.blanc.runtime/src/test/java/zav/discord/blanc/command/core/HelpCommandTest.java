package zav.discord.blanc.command.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.AbstractCommandTest;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.runtime.command.core.HelpCommand;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class HelpCommandTest extends AbstractCommandTest {
  private Command command;
  private static final String expected =
        "For a list of all commands and their function, please visit:\n" +
        "https://github.com/Zavarov/zav.discord.blanc/wiki";
  
  @BeforeEach
  public void setUp() {
    command = parse("b:help");
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(HelpCommand.class);
  }
  
  @Test
  public void testSendHelp() throws Exception {
    command.run();
    
    ArgumentCaptor<StringBuilder> stringCaptor = ArgumentCaptor.forClass(StringBuilder.class);
    
    verify(channelView, times(1)).send(stringCaptor.capture());
    
    assertThat(stringCaptor.getValue().toString()).isEqualTo(expected);
  }
}
