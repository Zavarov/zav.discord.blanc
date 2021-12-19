package zav.discord.blanc.command;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

public abstract class AbstractDevCommandTest extends AbstractCommandTest {
  protected Command command;
  
  @Test
  public void testCheckPermissions() throws InvalidCommandException {
    userValueObject.setRanks(List.of(Rank.DEVELOPER.name()));
    
    // No error
    command.validate();
  }
  
  @Test
  public void testCheckMissingPermission() {
    assertThrows(InsufficientRankException.class, () -> command.validate());
  }
}
