package zav.discord.blanc.command.parser;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class NumberArgumentTest {
  NumberArgument argument;
  
  @Before
  public void setUp() {
    argument = mock(NumberArgument.class);
    when(argument.asNumber()).thenReturn(Optional.of(BigDecimal.TEN));
    when(argument.asString()).thenCallRealMethod();
  }
  
  @Test
  public void testGetString() {
    assertThat(argument.asString()).contains("10");
  }
}
