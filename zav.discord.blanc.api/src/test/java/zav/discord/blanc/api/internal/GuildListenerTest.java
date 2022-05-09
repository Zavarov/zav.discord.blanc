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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.db.GuildTable;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.db.WebhookTable;

/**
 * Test class to check whether all entries to guilds and text channels are deleted from the database
 * once they are deleted or no longer accessible.
 */
@ExtendWith(MockitoExtension.class)
public class GuildListenerTest {
  
  GuildListener listener;
  
  @Mock WebhookTable webhookTable;
  @Mock TextChannelTable textChannelTable;
  @Mock GuildTable guildTable;
  @Mock TextChannel textChannel;
  @Mock Guild guild;
  @Mock GuildLeaveEvent leaveEvent;
  @Mock TextChannelDeleteEvent deleteEvent;
  
  /**
   * Initialize the {@link GuildListener} instance and assign all database tables.
   */
  @BeforeEach
  public void setUp() {
    listener = new GuildListener();
    listener.setDatabase(webhookTable);
    listener.setDatabase(textChannelTable);
    listener.setDatabase(guildTable);
  }
  
  @Test
  public void testOnGuildLeave() throws SQLException {
    when(leaveEvent.getGuild()).thenReturn(guild);
    listener.onGuildLeave(leaveEvent);
    verify(guildTable).delete(guild);
    verify(webhookTable).delete(guild);
    verify(textChannelTable).delete(guild);
  }
  
  @Test
  public void testErrorOnGuildLeave() throws SQLException {
    when(leaveEvent.getGuild()).thenReturn(guild);
    when(guildTable.delete(guild)).thenThrow(SQLException.class);
    listener.onGuildLeave(leaveEvent);
    verify(guildTable).delete(guild);
    verify(webhookTable, times(0)).delete(guild);
    verify(textChannelTable, times(0)).delete(guild);
  }
  
  @Test
  public void testOnTextChannelDelete() throws SQLException {
    when(deleteEvent.getChannel()).thenReturn(textChannel);
    listener.onTextChannelDelete(deleteEvent);
    verify(webhookTable).delete(textChannel);
    verify(textChannelTable).delete(textChannel);
  }
  
  @Test
  public void testErrorOnTextChannelDelete() throws SQLException {
    when(deleteEvent.getChannel()).thenReturn(textChannel);
    when(webhookTable.delete(textChannel)).thenThrow(SQLException.class);
    listener.onTextChannelDelete(deleteEvent);
    verify(webhookTable).delete(textChannel);
    verify(textChannelTable, times(0)).delete(textChannel);
  }
}
