package zav.discord.blanc.runtime.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.CommandProvider;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Test case to check whether the correct commands are returned for a given slash event.
 */
@ExtendWith(MockitoExtension.class)
public class SimpleCommandParserTest extends AbstractTest {
  
  @Mock Command command;
  @Mock CommandProvider provider;
  SimpleCommandParser parser;
  
  @BeforeEach
  public void setUp() {
    parser = new SimpleCommandParser(shard, provider);
  }
  
  @Test
  public void testParseCommand() {
    when(provider.create(any(), any())).thenReturn(Optional.of(command));
    
    Optional<Command> response = parser.parse(event);
    
    assertEquals(response.get(), command);
  }
  
  @Test
  public void testParseUnknownCommand() {
    Optional<Command> response = parser.parse(event);
    
    assertTrue(response.isEmpty());
  }
}
