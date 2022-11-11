package zav.discord.blanc.runtime.command.mod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.EnumSet;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Checks whether automatic responses can be added to the database.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ResponseAddCommandTest extends AbstractTest {
  @Mock OptionMapping pattern;
  @Mock OptionMapping answer;
  GuildCommandManager manager;
  ResponseAddCommand command;
  
  /**
   * Initializes the command with no arguments.
   */
  @BeforeEach
  public void setUp() {
    manager = new GuildCommandManager(shard, event);
    command = new ResponseAddCommand(event, manager);

    guildEntity.setAutoResponses(new ArrayList<>());
  }

  /**
   * Use Case: A valid pattern should be added to the list of auto responses..
   */
  @ParameterizedTest
  @CsvSource({
    "Hello There,General Kenobi",
    "^Hello There(!)?$, General Kenobi!",
    "(Foo),Bar"
  })
  public void testAddResponse(String source, String target) {
    when(event.getOption("pattern")).thenReturn(pattern);
    when(event.getOption("answer")).thenReturn(answer);
    when(pattern.getAsString()).thenReturn(source);
    when(answer.getAsString()).thenReturn(target);
    
    command.run();
    
    assertEquals(guildEntity.getAutoResponses().size(), 1);
    assertEquals(guildEntity.getAutoResponses().get(0).getPattern(), source);
    assertEquals(guildEntity.getAutoResponses().get(0).getAnswer(), target);

    verify(responseCache).invalidate(guild);
  }

  /**
   * Use Case: named-capturing groups are used internally and thus can't be in the input pattern.
   */
  @Test
  public void testPatternContainsNameCapturingGroup() {
    when(event.getOption("pattern")).thenReturn(pattern);
    when(event.getOption("answer")).thenReturn(answer);
    when(pattern.getAsString()).thenReturn("(?<g0>Hello There)");
    when(answer.getAsString()).thenReturn("General Kenobi");
    
    command.run();
    
    assertEquals(guildEntity.getAutoResponses().size(), 0);
    
    verify(responseCache, times(0)).invalidate(guild);
  }
  
  @Test
  public void testGetPermissions() {
    assertEquals(command.getPermissions(), EnumSet.of(Permission.MESSAGE_MANAGE));
  }
}
