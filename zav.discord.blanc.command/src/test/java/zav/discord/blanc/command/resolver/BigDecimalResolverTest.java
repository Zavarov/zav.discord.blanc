package zav.discord.blanc.command.resolver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.Argument;

/**
 * Test case for the decimal resolver.<br>
 * Verifies that the correct decimal value is retrieved from an argument.
 */
public class BigDecimalResolverTest {
  private Argument argument;
  private BigDecimalResolver resolver;
  
  /**
   * Initializes the resolver. Furthermore an argument that always returns the decimal value of
   * 10 is returned.
   */
  @BeforeEach
  public void setUp() {
    resolver = new BigDecimalResolver();
    
    argument = mock(Argument.class);
    when(argument.asNumber()).thenReturn(Optional.of(BigDecimal.TEN));
  }
  
  @Test
  public void testApply() {
    assertThat(resolver.apply(argument)).isEqualTo(BigDecimal.TEN);
  }
}
