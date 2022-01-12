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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.List;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import zav.discord.blanc.api.internal.listener.BlacklistListener;

public class BlacklistListenerTest {
  MockedStatic<PermissionUtil> mocked;
  
  long guildId = 11111L;
  long responseId = 22222L;
  
  Pattern pattern = Pattern.compile("banana");
  BlacklistListener listener;

  @Mock JDA jda;
  @Mock SelfUser selfUser;
  @Mock Message message;
  @Mock AuditableRestAction<Void> restAction;
  @Mock MessageEmbed messageEmbed;
  @Mock MessageEmbed.Field field;
  @Mock MessageEmbed.Footer footer;
  @Mock User user;
  @Mock Guild guild;
  @Mock TextChannel textChannel;
  @Mock Member selfMember;
  
  AutoCloseable closeable;
  
  GuildMessageReceivedEvent event;
  
  @BeforeEach
  public void setUp() {
    mocked = mockStatic(PermissionUtil.class);
    mocked.when(() -> PermissionUtil.checkPermission(any(), any(), any())).thenReturn(true);
    closeable = openMocks(this);
  
    when(jda.getSelfUser()).thenReturn(selfUser);
  
    when(user.getJDA()).thenReturn(jda);
    when(selfUser.getJDA()).thenReturn(jda);
    
    when(guild.getSelfMember()).thenReturn(selfMember);
    when(guild.getIdLong()).thenReturn(guildId);
    
    when(textChannel.getGuild()).thenReturn(guild);
    
    when(message.getGuild()).thenReturn(guild);
    when(message.getTextChannel()).thenReturn(textChannel);
    when(message.getAuthor()).thenReturn(user);
    when(message.delete()).thenReturn(restAction);
    when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(messageEmbed.getFields()).thenReturn(List.of(field));
    when(messageEmbed.getFooter()).thenReturn(footer);
  
    BlacklistListener.setPattern(guildId, pattern);
    
    listener = new BlacklistListener();
    event = new GuildMessageReceivedEvent(jda, responseId, message);
  }
  
  @AfterEach
  public void tearDown() throws Exception {
    mocked.close();
    closeable.close();
  }
  
  @Test
  public void testIgnoreOwnMessage() {
    // We've sent the message
    when(message.getAuthor()).thenReturn(selfUser);
    // Banned word
    when(message.getContentRaw()).thenReturn("banana");
    
    listener.onGuildMessageReceived(event);
    
    // Message shouldn't be deleted
    verify(message, times(0)).delete();
  }
  
  @Test
  public void testIgnoreWithoutPermissions() {
    // We're not allowed to delete the message
    mocked.when(() -> PermissionUtil.checkPermission(any(), any(), any())).thenReturn(false);
    // Banned word
    when(message.getContentRaw()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
  
    // Message shouldn't be deleted
    verify(message, times(0)).delete();
  }
  
  @Test
  public void testIgnoreWithoutPattern() {
    // Banned word
    when(message.getContentRaw()).thenReturn("banana");
    // Remove pattern
    BlacklistListener.setPattern(guildId, null);
  
    listener.onGuildMessageReceived(event);
  
    // Message shouldn't be deleted
    verify(message, times(0)).delete();
  }
  
  @Test
  public void testIgnoreValidMessage() {
    listener.onGuildMessageReceived(event);
  
    verify(message, times(0)).delete();
  }
  
  @Test
  public void testDeleteByTextContent() {
    // Banned word
    when(message.getContentRaw()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
    
    verify(message, times(1)).delete();
  }
  
  @Test
  public void testDeleteByEmbedTitle() {
    // Banned word
    when(messageEmbed.getTitle()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
  
  @Test
  public void testDeleteByEmbedUrl() {
    // Banned word
    when(messageEmbed.getUrl()).thenReturn("www.banana.com");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
  
  @Test
  public void testDeleteByEmbedDescription() {
    // Banned word
    when(messageEmbed.getDescription()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
  
  @Test
  public void testDeleteByEmbedFieldName() {
    // Banned word
    when(field.getName()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
  
  @Test
  public void testDeleteByEmbedFieldValue() {
    // Banned word
    when(field.getValue()).thenReturn("banana");
    
    listener.onGuildMessageReceived(event);
    
    verify(message, times(1)).delete();
  }
  
  @Test
  public void testDeleteByFooterText() {
    // Banned word
    when(footer.getText()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
}
