package zav.discord.blanc.runtime.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.CommandProvider;

/**
 * Test case to check whether the correct commands are returned for a given slash event.
 */
@ExtendWith(MockitoExtension.class)
public class SimpleCommandParserTest {
  
  @Mock Command command;
  @Mock CommandProvider provider;
  @Mock Client client;
  @Mock SlashCommandEvent event;
  SimpleCommandParser parser;
  
  @BeforeEach
  public void setUp() {
    parser = new SimpleCommandParser(client, provider);
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
