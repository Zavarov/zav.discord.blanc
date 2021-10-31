package zav.discord.blanc.command.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.AbstractDevCommandTest;
import zav.discord.blanc.runtime.command.dev.SayCommand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class SayCommandTest extends AbstractDevCommandTest {
  
  @BeforeEach
  public void setUp() {
    command = parse("b:dev.say \"Hallo Welt\"");
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(SayCommand.class);
  }
  
  @Test
  public void testSend() throws Exception {
    command.run();
    
    ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
  
    verify(channelView, times(1)).send(stringCaptor.capture());
    
    assertThat(stringCaptor.getValue()).isEqualTo("Hallo Welt");
  }
}
