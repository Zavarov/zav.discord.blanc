package zav.discord.blanc.command.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.command.parser.Argument;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class BigDecimalResolverTest {
  private Argument argument;
  private BigDecimalResolver resolver;
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
