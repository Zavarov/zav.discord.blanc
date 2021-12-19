package zav.discord.blanc.command.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.AbstractCommandTest;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.runtime.command.core.InviteCommand;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class InviteCommandTest extends AbstractCommandTest {
  private Command command;
  private static final String expected =
        "Use this link if you want to add this bot to your server:\n" +
        "https://discordapp.com/oauth2/authorize?client_id="+selfUserId+"&scope=bot";
  
  @BeforeEach
  public void setUp() {
    command = parse("b:invite");
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(InviteCommand.class);
  }
  
  @Test
  public void testSendInvite() throws Exception {
    command.run();
    
    ArgumentCaptor<StringBuilder> stringCaptor = ArgumentCaptor.forClass(StringBuilder.class);
    
    verify(channelView, times(1)).send(stringCaptor.capture());
    
    assertThat(stringCaptor.getValue().toString()).isEqualTo(expected);
  }
}
