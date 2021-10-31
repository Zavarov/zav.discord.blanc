package zav.discord.blanc.command.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.AbstractDevCommandTest;
import zav.discord.blanc.runtime.command.dev.ReactionCommand;
import zav.discord.blanc.view.UserView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

public class ReactionCommandTest  extends AbstractDevCommandTest {
  
  @BeforeEach
  public void setUp() {
    command = parse("b:dev.react %s %s", messageId, "foo");
    
    UserView userView = mock(UserView.class);
    
    when(shardView.getUser(any())).thenReturn(userView);
    when(userView.getAbout()).thenReturn(user);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(ReactionCommand.class);
  }
  
  @Test
  public void testSend() throws Exception {
    command.run();
    
    ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
    
    verify(messageView, times(1)).react(stringCaptor.capture());
    
    assertThat(stringCaptor.getValue()).isEqualTo("foo");
  }
}
