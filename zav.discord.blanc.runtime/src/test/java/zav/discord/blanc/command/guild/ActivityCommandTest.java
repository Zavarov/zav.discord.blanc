package zav.discord.blanc.command.guild;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.AbstractCommandTest;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.runtime.command.guild.ActivityCommand;

import java.awt.image.BufferedImage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class ActivityCommandTest extends AbstractCommandTest {
  private Command command;
  private BufferedImage image;
  
  @BeforeEach
  public void setUp() {
    command = parse("b:activity %s", channelId);
    image = mock(BufferedImage.class);
    
    when(guild.getActivity(any())).thenReturn(image);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(ActivityCommand.class);
  }
  
  @Test
  public void testSendImage() throws Exception {
    command.run();
  
    ArgumentCaptor<BufferedImage> imageCaptor = ArgumentCaptor.forClass(BufferedImage.class);
    ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
    
    verify(channelView, times(1)).send(imageCaptor.capture(), titleCaptor.capture());
    
    assertThat(imageCaptor.getValue()).isEqualTo(image);
    assertThat(titleCaptor.getValue()).isEqualTo("Activity.png");
  }
}
