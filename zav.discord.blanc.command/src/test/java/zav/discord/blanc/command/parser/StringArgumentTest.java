package zav.discord.blanc.command.parser;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class StringArgumentTest {
  StringArgument argument;
  
  @Before
  public void setUp() {
    argument = mock(StringArgument.class);
    when(argument.asNumber()).thenCallRealMethod();
  }
  
  @Test
  public void testGetInvalidNumber() {
    when(argument.asString()).thenReturn(Optional.of("x"));
    assertThat(argument.asNumber()).isEmpty();
  }
  
  @Test
  public void testGetNumber() {
    when(argument.asString()).thenReturn(Optional.of("21"));
    assertThat(argument.asNumber()).contains(BigDecimal.valueOf(21));
    
    when(argument.asString()).thenReturn(Optional.of("3.14"));
    assertThat(argument.asNumber()).contains(BigDecimal.valueOf(3.14));
  }
}
