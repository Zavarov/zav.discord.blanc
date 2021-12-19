package zav.discord.blanc.command.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.AbstractDevCommandTest;
import zav.discord.blanc.databind.UserValueObject;
import zav.discord.blanc.runtime.command.dev.UserCommand;
import zav.discord.blanc.api.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

public class UserCommandTest extends AbstractDevCommandTest {
  
  @BeforeEach
  public void setUp() {
    command = parse("b:dev.user %s", userId);
    
    User user = mock(User.class);
    
    when(shard.getUser(any())).thenReturn(user);
    when(user.getAbout()).thenReturn(this.userValueObject);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(UserCommand.class);
  }
  
  @Test
  public void testSend() throws Exception {
    command.run();
    
    ArgumentCaptor<UserValueObject> userCaptor = ArgumentCaptor.forClass(UserValueObject.class);
    
    verify(channelView, times(1)).send(userCaptor.capture());
    
    assertThat(userCaptor.getValue()).isEqualTo(userValueObject);
  }
}
