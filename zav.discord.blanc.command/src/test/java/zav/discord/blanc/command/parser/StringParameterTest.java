package zav.discord.blanc.command.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for the string argument.<br>
 * Verifies that the correct decimal and string representation is retrieved from an argument.
 */
public class StringParameterTest {
  StringParameter argument;
  
  @BeforeEach
  public void setUp() {
    argument = mock(StringParameter.class);
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
