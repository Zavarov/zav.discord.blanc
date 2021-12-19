package zav.discord.blanc.command.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.command.AbstractCommandTest;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.databind.io.CredentialsValueObject;
import zav.discord.blanc.runtime.command.core.SupportCommand;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class SupportCommandTest  extends AbstractCommandTest {
  private Command command;
  private static final String expected =
        "If you have any questions, hit me up in the support server:\n" +
        url;
  
  @BeforeEach
  public void setUp() {
    command = parse("b:support");
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(SupportCommand.class);
  }
  
  @Test
  public void testSendSupportLink() throws Exception {
    command.run();
    
    ArgumentCaptor<StringBuilder> stringCaptor = ArgumentCaptor.forClass(StringBuilder.class);
    
    verify(channelView, times(1)).send(stringCaptor.capture());
    
    assertThat(stringCaptor.getValue().toString()).isEqualTo(expected);
  }
}
