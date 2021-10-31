package zav.discord.blanc.command.core;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.AbstractCommandTest;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.runtime.command.core.MathCommand;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MathCommandTest extends AbstractCommandTest {
  private static final BigDecimal expected = BigDecimal.valueOf(Math.sin(Math.PI));
  private static final Percentage offset = Percentage.withPercentage(1e-15);
  private Command command;
  
  @BeforeEach
  public void setUp() {
    command = parse("b:math sin(pi)");
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(MathCommand.class);
  }
  
  @Test
  public void testSendSolution() throws Exception {
    command.run();
    
    ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
    
    verify(channelView, times(1)).send(stringCaptor.capture());
    
    assertThat(new BigDecimal(stringCaptor.getValue())).isCloseTo(expected, offset);
  }
}
