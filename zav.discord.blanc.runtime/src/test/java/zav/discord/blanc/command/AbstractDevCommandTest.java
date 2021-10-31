package zav.discord.blanc.command;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import zav.discord.blanc.Rank;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class AbstractDevCommandTest extends AbstractCommandTest {
  protected Command command;
  
  @Test
  public void testCheckPermissions() throws InvalidCommandException {
    user.getRanks().add(Rank.DEVELOPER.name());
    
    // No error
    command.validate();
  }
  
  @Test
  public void testCheckMissingPermission() {
    assertThrows(InsufficientRankException.class, () -> command.validate());
  }
}
