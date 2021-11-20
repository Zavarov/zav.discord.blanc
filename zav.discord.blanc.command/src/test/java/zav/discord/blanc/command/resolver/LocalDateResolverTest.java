package zav.discord.blanc.command.resolver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.Argument;

/**
 * Test case for the date resolver.<br>
 * Verifies that the correct date value is retrieved from an argument.
 */
public class LocalDateResolverTest {
  private Argument argument;
  private LocalDateResolver resolver;
  
  /**
   * Initializes the resolver. Furthermore an argument that always returns the date July 13th, 1991.
   */
  @BeforeEach
  public void setUp() {
    resolver = new LocalDateResolver();
    
    argument = mock(Argument.class);
    when(argument.asString()).thenReturn(Optional.of("1991-07-13"));
  }
  
  @Test
  public void testApply() {
    LocalDate date = resolver.apply(argument);
    assertThat(date.getDayOfMonth()).isEqualTo(13);
    assertThat(date.getMonthValue()).isEqualTo(7);
    assertThat(date.getYear()).isEqualTo(1991);
  }
  
  @Test
  public void testApplyInvalidDate() {
    when(argument.asString()).thenReturn(Optional.of("x"));
    
    assertThrows(IllegalArgumentException.class, () -> resolver.apply(argument));
  }
}
