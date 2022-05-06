/*
 * Copyright (c) 2022 Zavarov.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package zav.discord.blanc.runtime.command.mod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.runtime.command.AbstractGuildCommandTest;

/**
 * Checks whether it is possible to blacklist/whitelist regular expressions.
 */
public class BlacklistCommandTest extends AbstractGuildCommandTest {
  private @Mock OptionMapping arg;
  
  /**
   * Tests whether the expression has been blacklisted.
   */
  @Test
  public void testAddRegEx() throws Exception {
    when(guild.getName()).thenReturn(guildEntity.getName());
    when(guild.getIdLong()).thenReturn(guildEntity.getId());
    when(event.replyFormat(anyString(), anyString())).thenReturn(reply);
    when(event.getOption(anyString())).thenReturn(arg);
    when(arg.getAsString()).thenReturn("test");
    
    guildTable.put(guildEntity);
    
    run(BlacklistCommand.class);
    
    // Has the database been updated?
    GuildEntity response = get(guildTable, guildEntity.getId());
    
    assertThat(response.getId()).isEqualTo(guildEntity.getId());
    assertThat(response.getName()).isEqualTo(guildEntity.getName());
    assertThat(response.getPrefix()).isEqualTo(guildEntity.getPrefix());
    assertThat(response.getBlacklist()).containsExactly("foo", "bar", "test");
  }
  
  /**
   * Tests whether the expression has been whitelisted.
   */
  @Test
  public void testRemoveRegEx() throws Exception {
    when(guild.getName()).thenReturn(guildEntity.getName());
    when(guild.getIdLong()).thenReturn(guildEntity.getId());
    when(event.replyFormat(anyString(), anyString())).thenReturn(reply);
    when(event.getOption(anyString())).thenReturn(arg);
    when(arg.getAsString()).thenReturn("foo");
    
    guildTable.put(guildEntity);
  
    run(BlacklistCommand.class);
  
    // Has the database been updated?
    GuildEntity response = get(guildTable, guildEntity.getId());
  
    assertThat(response.getId()).isEqualTo(guildEntity.getId());
    assertThat(response.getName()).isEqualTo(guildEntity.getName());
    assertThat(response.getPrefix()).isEqualTo(guildEntity.getPrefix());
    assertThat(response.getBlacklist()).containsExactly("bar");
  }
}
