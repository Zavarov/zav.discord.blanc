package zav.discord.blanc.runtime.command.mod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.runtime.command.AbstractDatabaseTest;

/**
 * Checks whether automatic responses can be added to the database.
 */
@ExtendWith(MockitoExtension.class)
public class ResponseAddCommandTest extends AbstractDatabaseTest<GuildEntity> {
  @Mock OptionMapping pattern;
  @Mock OptionMapping answer;
  GuildCommandManager manager;
  ResponseAddCommand command;
  
  /**
   * Initializes the command with no arguments.
   */
  @BeforeEach
  public void setUp() {
    super.setUp(new GuildEntity());
    
    when(entityManager.find(eq(GuildEntity.class), any())).thenReturn(entity);

    manager = new GuildCommandManager(client, event);
    command = new ResponseAddCommand(event, manager);
  }

  /**
   * Use Case: A valid pattern should be added to the list of auto responses..
   */
  @Test
  public void testAddResponse() {
    when(event.getOption("pattern")).thenReturn(pattern);
    when(event.getOption("answer")).thenReturn(answer);
    when(pattern.getAsString()).thenReturn("Hello There");
    when(answer.getAsString()).thenReturn("General Kenobi");
    
    command.run();
    
    assertEquals(entity.getAutoResponses().size(), 1);
    assertEquals(entity.getAutoResponses().get(0).getPattern(), "Hello There");
    assertEquals(entity.getAutoResponses().get(0).getAnswer(), "General Kenobi");

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
    
    assertEquals(entity.getAutoResponses().size(), 0);
    
    verify(responseCache, times(0)).invalidate(guild);
  }
}
