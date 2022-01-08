package zav.discord.blanc.command.guild.mod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.api.Permission;
import zav.discord.blanc.command.*;
import zav.discord.blanc.databind.GuildDto;
import zav.discord.blanc.db.GuildDatabase;
import zav.discord.blanc.runtime.command.guild.mod.PrefixCommand;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class PrefixCommandTest extends AbstractCommandTest {
  private Command command;
  
  @BeforeEach
  public void setUp() {
    command = parse("b:mod.prefix foo");
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(PrefixCommand.class);
  }
  
  /**
   * Tests whether guild-prefix has been overwritten.
   */
  @Test
  public void testSetPrefix() throws Exception {
    command = parse("b:mod.prefix foo");
    
    command.run();
    
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
    
    verify(channelView, times(1)).send(msgCaptor.capture(), argCaptor.capture());
    
    // Correct message?
    assertThat(msgCaptor.getValue()).isEqualTo("Set the custom prefix to '%s'.");
    assertThat(argCaptor.getValue()).isEqualTo("foo");
    
    // Has the guild been updated?
    assertThat(guildDto.getPrefix()).contains("foo");
    
    // Has the database been updated?
    GuildDto dbGuild = GuildDatabase.get(guildId);
    
    assertThat(dbGuild.getId()).isEqualTo(guildId);
    assertThat(dbGuild.getName()).isEqualTo(guildName);
    assertThat(dbGuild.getPrefix()).contains("foo");
    assertThat(dbGuild.getBlacklist()).isEmpty();
  }
  
  /**
   * Tests whether guild-prefix has been removed.
   */
  @Test
  public void testRemovePrefix() throws Exception {
    command = parse("b:mod.prefix");
  
    command.run();
  
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
  
    verify(channelView, times(1)).send(msgCaptor.capture());
  
    // Correct message?
    assertThat(msgCaptor.getValue()).isEqualTo("Removed the custom prefix.");
  
    // Has the guild been updated?
    assertThat(guildDto.getPrefix()).isEmpty();
  
    // Has the database been updated?
    GuildDto dbGuild = GuildDatabase.get(guildId);
  
    assertThat(dbGuild.getId()).isEqualTo(guildId);
    assertThat(dbGuild.getName()).isEqualTo(guildName);
    assertThat(dbGuild.getPrefix()).isEmpty();
    assertThat(dbGuild.getBlacklist()).isEmpty();
  }
  
  @Test
  public void testCheckPermissions() throws InvalidCommandException {
    when(member.getPermissions()).thenReturn(Set.of(Permission.MANAGE_MESSAGES));
    
    // No error
    command.validate();
  }
  
  @Test
  public void testCheckMissingPermission() {
    assertThrows(InsufficientPermissionException.class, () -> command.validate());
  }
}
