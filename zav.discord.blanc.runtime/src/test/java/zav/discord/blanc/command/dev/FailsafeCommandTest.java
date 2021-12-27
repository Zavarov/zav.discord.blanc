package zav.discord.blanc.command.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.command.Rank;
import zav.discord.blanc.command.AbstractDevCommandTest;
import zav.discord.blanc.databind.UserValueObject;
import zav.discord.blanc.db.UserDatabase;
import zav.discord.blanc.runtime.command.dev.FailsafeCommand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class FailsafeCommandTest extends AbstractDevCommandTest {
  @BeforeEach
  public void setUp() {
    command = parse("b:dev.failsafe");
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(FailsafeCommand.class);
  }
  
  @Test
  public void testBecomeRoot() throws Exception {
    userValueObject.getRanks().add(Rank.DEVELOPER.name());

    command.run();
  
    // Send response
    verify(channelView, times(1)).send(anyString(), anyString());
    
    // Has the user been updated
    assertThat(userValueObject.getRanks()).contains(Rank.ROOT.name());
  
    // Has the database been updated?
    UserValueObject dbUser = UserDatabase.get(userId);
  
    assertThat(dbUser.getId()).isEqualTo(userId);
    assertThat(dbUser.getName()).isEqualTo(userName);
    assertThat(dbUser.getDiscriminator()).isEqualTo(userDiscriminator);
    assertThat(dbUser.getRanks()).contains(Rank.ROOT.name());
  }
  
  @Test
  public void testBecomeDeveloper() throws Exception {
    userValueObject.getRanks().add(Rank.ROOT.name());
  
    command.run();
  
    // Send response
    verify(channelView, times(1)).send(anyString(), anyString());
  
    // Has the user been updated
    assertThat(userValueObject.getRanks()).contains(Rank.DEVELOPER.name());
  
    // Has the database been updated?
    UserValueObject dbUser = UserDatabase.get(userId);
  
    assertThat(dbUser.getId()).isEqualTo(userId);
    assertThat(dbUser.getName()).isEqualTo(userName);
    assertThat(dbUser.getDiscriminator()).isEqualTo(userDiscriminator);
    assertThat(dbUser.getRanks()).contains(Rank.DEVELOPER.name());
  
  }
}
