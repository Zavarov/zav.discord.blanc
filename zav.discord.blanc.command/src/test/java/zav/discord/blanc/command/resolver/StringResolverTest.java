package zav.discord.blanc.command.resolver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.Argument;

/**
 * Test case for the string resolver.<br>
 * Verifies that the string is retrieved from an argument.
 */
public class StringResolverTest {
  private Argument argument;
  private StringResolver resolver;
  
  /**
   * Initializes the resolver. Furthermore an argument that always returns string <i>Foo</i>.
   */
  @BeforeEach
  public void setUp() {
    resolver = new StringResolver();
    
    argument = mock(Argument.class);
    when(argument.asString()).thenReturn(Optional.of("Foo"));
  }
  
  @Test
  public void testApply() {
    assertThat(resolver.apply(argument)).isEqualTo("Foo");
  }
}
