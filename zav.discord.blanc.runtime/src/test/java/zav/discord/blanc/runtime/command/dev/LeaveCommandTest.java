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

package zav.discord.blanc.runtime.command.dev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static zav.test.io.JsonUtils.read;

import net.dv8tion.jda.api.requests.RestAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.db.GuildTable;
import zav.discord.blanc.runtime.command.AbstractDevCommandTest;

/**
 * Checks whether the database is cleared whenever the bot leaves a guild.
 */
@ExtendWith(MockitoExtension.class)
public class LeaveCommandTest extends AbstractDevCommandTest {
  
  private @Mock RestAction<Void> action;
  private GuildTable guildTable;
  private GuildEntity guildEntity;
  
  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
  
    guildEntity = read("Guild.json", GuildEntity.class);
    guildTable = injector.getInstance(GuildTable.class);
    guildTable.put(guildEntity);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    check("b:dev.leave", LeaveCommand.class);
  }
  
  @Test
  public void testLeaveGuild() throws Exception {
    when(shard.getGuildById(anyLong())).thenReturn(guild);
    when(guild.getIdLong()).thenReturn(guildEntity.getId());
    when(guild.leave()).thenReturn(action);

    run("b:dev.leave %s", guildEntity.getId());
    
    verify(guild, times(1)).leave();
    
    // Has the database been cleared?
    assertThat(guildTable.contains(guildEntity.getId())).isFalse();
  }
}
