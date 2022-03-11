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

import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.db.GuildTable;
import zav.discord.blanc.runtime.command.*;
import zav.discord.blanc.runtime.command.AbstractCommandTest;
import zav.test.io.JsonUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static zav.test.io.JsonUtils.read;

@ExtendWith(MockitoExtension.class)
public class PrefixCommandTest extends AbstractCommandTest {
  private @Mock MessageAction action;
  
  private GuildTable guildTable;
  private GuildEntity guildEntity;
  
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    
    guildTable = injector.getInstance(GuildTable.class);
    guildEntity = read("Guild.json", GuildEntity.class);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    check("b:mod.prefix foo", PrefixCommand.class);
  }
  
  /**
   * Tests whether guild-prefix has been overwritten.
   */
  @Test
  public void testSetPrefix() throws Exception {
    when(guild.getName()).thenReturn(guildEntity.getName());
    when(guild.getIdLong()).thenReturn(guildEntity.getId());
    when(textChannel.sendMessageFormat(anyString(), anyString())).thenReturn(action);
    
    run("b:mod.prefix foo");
    
    // Has the database been updated?
    GuildEntity response = get(guildTable, guildEntity.getId());
    
    assertThat(response.getId()).isEqualTo(guildEntity.getId());
    assertThat(response.getName()).isEqualTo(guildEntity.getName());
    assertThat(response.getPrefix()).contains("foo");
    assertThat(response.getBlacklist()).isEmpty();
  }
  
  /**
   * Tests whether guild-prefix has been removed.
   */
  @Test
  public void testRemovePrefix() throws Exception {
    when(guild.getName()).thenReturn(guildEntity.getName());
    when(guild.getIdLong()).thenReturn(guildEntity.getId());
    when(textChannel.sendMessage(anyString())).thenReturn(action);
    
    run("b:mod.prefix");
  
    // Has the database been updated?
    GuildEntity response = get(guildTable, guildEntity.getId());
  
    assertThat(response.getId()).isEqualTo(guildEntity.getId());
    assertThat(response.getName()).isEqualTo(guildEntity.getName());
    assertThat(response.getPrefix()).isEmpty();
    assertThat(response.getBlacklist()).isEmpty();
  }
}
