package zav.discord.blanc.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.command.internal.RankValidator;
import zav.discord.blanc.databind.Rank;

/**
 * This test case checks whether the utility functions provided by the command manager are
 * functional.
 */
@ExtendWith(MockitoExtension.class)
public class CommandManagerTest {
  @Mock InsufficientRankException exception;
  @Mock Client client;
  @Mock SlashCommandEvent event;
  CommandManager manager;
  
  /**
   * Initializes the CommandManager instance with a mocked rank validator.
   */
  @BeforeEach
  public void setUp() {
    try (var mocked = mockConstruction(RankValidator.class)) {
      manager = new CommandManager(client, event);
    }
  }
  
  @Test
  public void testValidate() throws InsufficientRankException {
    // RankValidator has been mocked, hence the call should proceed without any error
    manager.validate(Rank.ROOT);
  }
  
  @Test
  public void testGetClient() {
    assertEquals(manager.getClient(), client);
  }
}
