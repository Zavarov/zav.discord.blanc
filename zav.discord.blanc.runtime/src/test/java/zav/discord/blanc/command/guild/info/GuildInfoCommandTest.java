package zav.discord.blanc.command.guild.info;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.AbstractCommandTest;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.databind.Guild;
import zav.discord.blanc.runtime.command.guild.info.GuildInfoCommand;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class GuildInfoCommandTest extends AbstractCommandTest {
  private Command command;
  
  @BeforeEach
  public void setUp() {
    command = parse("b:guild %s", guildId);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(GuildInfoCommand.class);
  }
  
  @Test
  public void testSendGuild() throws Exception {
    command.run();
  
    ArgumentCaptor<Guild> guildCaptor = ArgumentCaptor.forClass(Guild.class);
    
    verify(channelView, times(1)).send(guildCaptor.capture());
    
    assertThat(guildCaptor.getValue()).isEqualTo(guild);
  }
}
