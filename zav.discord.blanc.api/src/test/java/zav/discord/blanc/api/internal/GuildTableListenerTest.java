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

package zav.discord.blanc.api.internal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.db.GuildTable;

/**
 * Checks whether the guild database is cleared whenever the bot leaves a guild.
 */
@ExtendWith(MockitoExtension.class)
public class GuildTableListenerTest {
  
  GuildTableListener listener;
  
  @Mock GuildTable db;
  @Mock Guild guild;
  @Mock GuildLeaveEvent leaveEvent;
  
  @BeforeEach
  public void setUp() {
    listener = new GuildTableListener(db);
  }
  
  /**
   * Use Case: When leaving a guild, all entries should be deleted from the database.
   *
   * @throws SQLException If a database error occurred.
   */
  @Test
  public void testOnGuildLeave() throws SQLException {
    when(leaveEvent.getGuild()).thenReturn(guild);
    listener.onGuildLeave(leaveEvent);
    verify(db).delete(guild);
  }
  
  /**
   * Use Case: When leaving a guild, while the database is unavailable, nothing should happen. The
   * entries are kept in the database and have to be cleaned by either restarting the bot or by
   * joining and leaving the guild again.
   *
   * @throws SQLException If a database error occurred.
   */
  @Test
  public void testErrorOnGuildLeave() throws SQLException {
    when(leaveEvent.getGuild()).thenReturn(guild);
    when(db.delete(guild)).thenThrow(SQLException.class);
    listener.onGuildLeave(leaveEvent);
    verify(db).delete(guild);
  }
}
