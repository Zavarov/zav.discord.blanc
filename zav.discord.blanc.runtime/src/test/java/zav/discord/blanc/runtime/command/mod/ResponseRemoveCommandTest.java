package zav.discord.blanc.runtime.command.mod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Checks whether automatic responses can be removed to the database.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ResponseRemoveCommandTest extends AbstractTest {
  @Mock OptionMapping index;
  GuildCommandManager manager;
  ResponseRemoveCommand command;
  
  /**
   * Initializes the command with a single response.
   */
  @BeforeEach
  public void setUp() {
    responseEntity.setPattern("Hello There");
    responseEntity.setAnswer("General Kenobi");
    
    manager = new GuildCommandManager(client, event);
    command = new ResponseRemoveCommand(event, manager);
  }

  /**
   * Use Case: A valid index should be removed from the list of auto responses..
   */
  @Test
  public void testRemoveResponse() {
    when(event.getOption("index")).thenReturn(index);
    when(index.getAsLong()).thenReturn(0L);
    
    command.run();
    
    assertEquals(guildEntity.getAutoResponses().size(), 0);

    verify(responseCache).invalidate(guild);
  }

  /**
   * Use Case: The database should be modified when an invalid index is selected.
   */
  @ParameterizedTest
  @ValueSource(longs = {-1, 2})
  public void testIgnoreInvalidIndex(long realIndex) {
    when(event.getOption("index")).thenReturn(index);
    when(index.getAsLong()).thenReturn(realIndex);
    
    command.run();
    
    assertEquals(guildEntity.getAutoResponses().size(), 1);
    assertEquals(guildEntity.getAutoResponses().get(0).getPattern(), "Hello There");
    assertEquals(guildEntity.getAutoResponses().get(0).getAnswer(), "General Kenobi");

    verify(responseCache, times(0)).invalidate(guild);
  }
  
  @Test
  public void testGetPermissions() {
    assertEquals(command.getPermissions(), EnumSet.of(Permission.MESSAGE_MANAGE));
  }
}
