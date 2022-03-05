package zav.discord.blanc.command.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for the number argument.<br>
 * Verifies that the correct decimal and string representation is retrieved from an argument.
 */
public class NumberParameterTest {
  NumberParameter argument;
  
  /**
   * Initializes the resolver. Furthermore, an argument that always returns the decimal value of
   * 10 and the corresponding string is returned.
   */
  @BeforeEach
  public void setUp() {
    argument = mock(NumberParameter.class);
    when(argument.asNumber()).thenReturn(Optional.of(BigDecimal.TEN));
    when(argument.asString()).thenCallRealMethod();
  }
  
  @Test
  public void testGetString() {
    assertThat(argument.asString()).contains("10");
  }
}