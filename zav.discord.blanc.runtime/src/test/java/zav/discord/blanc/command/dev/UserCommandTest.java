package zav.discord.blanc.command.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.AbstractDevCommandTest;
import zav.discord.blanc.databind.User;
import zav.discord.blanc.runtime.command.dev.UserCommand;
import zav.discord.blanc.view.UserView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

public class UserCommandTest extends AbstractDevCommandTest {
  
  @BeforeEach
  public void setUp() {
    command = parse("b:dev.user %s", userId);
    
    UserView userView = mock(UserView.class);
    
    when(shardView.getUser(any())).thenReturn(userView);
    when(userView.getAbout()).thenReturn(user);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(UserCommand.class);
  }
  
  @Test
  public void testSend() throws Exception {
    command.run();
    
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    
    verify(channelView, times(1)).send(userCaptor.capture());
    
    assertThat(userCaptor.getValue()).isEqualTo(user);
  }
}
