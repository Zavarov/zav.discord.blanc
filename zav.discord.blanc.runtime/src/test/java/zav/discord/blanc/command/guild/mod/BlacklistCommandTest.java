package zav.discord.blanc.command.guild.mod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.api.Permission;
import zav.discord.blanc.command.*;
import zav.discord.blanc.databind.GuildDto;
import zav.discord.blanc.db.GuildDatabase;
import zav.discord.blanc.runtime.command.guild.mod.BlacklistCommand;

import java.util.Set;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class BlacklistCommandTest extends AbstractCommandTest {
  private Command command;
  
  @BeforeEach
  public void setUp() {
    command = parse("b:mod.blacklist bar");
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(BlacklistCommand.class);
  }
  
  /**
   * Tests whether the expression has been blacklisted.
   */
  @Test
  public void testAddRegEx() throws Exception {
    command.run();
    
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
  
    verify(channelView, times(1)).send(msgCaptor.capture(), argCaptor.capture());
    
    // Correct message?
    assertThat(msgCaptor.getValue()).isEqualTo("Added '%s' to the blacklist.");
    assertThat(argCaptor.getValue()).isEqualTo("bar");
    
    // Has the guild been updated?
    assertThat(guildDto.getBlacklist()).containsExactly("bar");
    
    // Has the view been updated?
    ArgumentCaptor<Pattern> patternCaptor = ArgumentCaptor.forClass(Pattern.class);
    verify(guild, times(1)).updateBlacklist(patternCaptor.capture());
    
    assertThat(patternCaptor.getValue().pattern()).isEqualTo("bar");
    
    // Has the database been updated?
    GuildDto dbGuild = GuildDatabase.get(guildId);
    
    assertThat(dbGuild.getId()).isEqualTo(guildId);
    assertThat(dbGuild.getName()).isEqualTo(guildName);
    assertThat(dbGuild.getPrefix()).contains(guildPrefix);
    assertThat(dbGuild.getBlacklist()).containsExactly("bar");
  }
  
  /**
   * Tests whether the expression has been whitelisted.
   */
  @Test
  public void testRemoveRegEx() throws Exception {
    guildDto.getBlacklist().add("bar");
    
    command.run();
  
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
  
    verify(channelView, times(1)).send(msgCaptor.capture(), argCaptor.capture());
  
    // Correct message?
    assertThat(msgCaptor.getValue()).isEqualTo("Removed '%s' from the blacklist.");
    assertThat(argCaptor.getValue()).isEqualTo("bar");
  
    // Has the guild been updated?
    assertThat(guildDto.getBlacklist()).isEmpty();
  
    // Has the view been updated?
    ArgumentCaptor<Pattern> patternCaptor = ArgumentCaptor.forClass(Pattern.class);
    verify(guild, times(1)).updateBlacklist(patternCaptor.capture());
  
    assertThat(patternCaptor.getValue().pattern()).isEmpty();
  
    // Has the database been updated?
    GuildDto dbGuild = GuildDatabase.get(guildId);
  
    assertThat(dbGuild.getId()).isEqualTo(guildId);
    assertThat(dbGuild.getName()).isEqualTo(guildName);
    assertThat(dbGuild.getPrefix()).contains(guildPrefix);
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
