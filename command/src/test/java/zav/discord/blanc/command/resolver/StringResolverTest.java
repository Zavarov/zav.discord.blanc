package zav.discord.blanc.command.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.command.parser.Argument;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class StringResolverTest {
  private Argument argument;
  private StringResolver resolver;
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
