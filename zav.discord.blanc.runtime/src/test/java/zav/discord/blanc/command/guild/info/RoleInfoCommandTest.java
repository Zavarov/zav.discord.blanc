package zav.discord.blanc.command.guild.info;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.AbstractCommandTest;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.databind.RoleDto;
import zav.discord.blanc.runtime.command.guild.info.RoleInfoCommand;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RoleInfoCommandTest extends AbstractCommandTest {
  private Command command;
  
  @BeforeEach
  public void setUp() {
    command = parse("b:role %s", roleId);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(RoleInfoCommand.class);
  }
  
  @Test
  public void testSendGuild() throws Exception {
    command.run();
    
    ArgumentCaptor<RoleDto> roleCaptor = ArgumentCaptor.forClass(RoleDto.class);
    
    verify(channelView, times(1)).send(roleCaptor.capture());
    
    assertThat(roleCaptor.getValue()).isEqualTo(roleDto);
  }
}
