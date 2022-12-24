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

package zav.discord.blanc.api.listener;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;

/**
 * Checks whether the text channel database is updated whenever the bot leaves a guild or a text
 * channel is deleted.
 */
@ExtendWith(MockitoExtension.class)
public class TextChannelListenerTest {
  
  TextChannelListener listener;
  TextChannelEntity channelEntity;
  GuildEntity guildEntity;
  
  @Mock TextChannel textChannel;
  @Mock Guild guild;
  @Mock GuildLeaveEvent leaveEvent;
  @Mock TextChannelDeleteEvent deleteEvent;
  MockedStatic<GuildEntity> mocked1;
  MockedStatic<TextChannelEntity> mocked2;
  
  /**
   * Initializes the text channel listener.<br>
   * The database used by the listener is initialized with the entities {@code Webhook.json},
   * {@code TextChannel.json} and {@code Guild.json}.
   */
  @BeforeEach
  public void setUp() {
    listener = new TextChannelListener();
    
    channelEntity = new TextChannelEntity();
    guildEntity = new GuildEntity();
    
    mocked1 = mockStatic(GuildEntity.class);
    mocked1.when(() -> GuildEntity.find(guild)).thenReturn(guildEntity);

    mocked2 = mockStatic(TextChannelEntity.class);
    mocked2.when(() -> TextChannelEntity.find(textChannel)).thenReturn(channelEntity);
  }
  
  @AfterEach
  public void tearDown() {
    mocked1.close();
    mocked2.close();
  }
  
  /**
   * Use Case: When leaving a guild, all entries should be deleted from the database.
   */
  @Test
  public void testOnGuildLeave() {    
    when(leaveEvent.getGuild()).thenReturn(guild);

    listener.onGuildLeave(leaveEvent);

    mocked1.verify(() -> GuildEntity.remove(guild));
  }
  
  /**
   * Use Case: When a text channel is deleted, all corresponding entries should be deleted from the
   * database.
   */
  @Test
  public void testOnTextChannelDelete() {
    when(deleteEvent.getChannel()).thenReturn(textChannel);

    listener.onTextChannelDelete(deleteEvent);

    mocked2.verify(() -> TextChannelEntity.remove(guild), times(0));
    mocked2.verify(() -> TextChannelEntity.remove(textChannel));
  }
}
