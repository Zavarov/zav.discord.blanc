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
import static zav.test.io.JsonUtils.read;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.db.GuildDatabaseTable;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Test class for checking whether forbidden message are deleted automatically.
 */
@ExtendWith(MockitoExtension.class)
public class BlacklistListenerTest extends AbstractListenerTest {
  MockedStatic<PermissionUtil> permissions;

  BlacklistListener listener;
  GuildEntity data;
  GuildDatabaseTable db;

  @Mock JDA jda;
  @Mock SelfUser selfUser;
  @Mock Message message;
  @Mock AuditableRestAction<Void> restAction;
  @Mock MessageEmbed messageEmbed;
  @Mock MessageEmbed.Field field;
  @Mock MessageEmbed.Footer footer;
  @Mock User author;
  @Mock Guild guild;
  @Mock TextChannel textChannel;
  @Mock GuildMessageReceivedEvent event;
  
  /**
   * Initializes a fictitious GuildMessageReceivedEvent.
   */
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    
    data = read("Guild.json", GuildEntity.class);
    
    db = injector.getInstance(GuildDatabaseTable.class);
    db.put(data);
    
    permissions = mockStatic(PermissionUtil.class);
    permissions.when(() -> PermissionUtil.checkPermission(any(), any(), any())).thenReturn(true);
    
    listener = injector.getInstance(BlacklistListener.class);
  }
  
  /**
   * Close all static mocks.
   *
   * @throws Exception When some mocks couldn't be closed.
   */
  @AfterEach
  public void tearDown() throws Exception {
    permissions.close();
  
    Files.deleteIfExists(SqlQuery.ENTITY_DB_PATH);
    Files.deleteIfExists(SqlQuery.ENTITY_DB_PATH.getParent());
  }
  
  @Test
  public void testIgnoreOwnMessage() {
    // We've sent the message
    when(event.getAuthor()).thenReturn(selfUser);
    when(selfUser.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    
    listener.onGuildMessageReceived(event);
    
    // Message shouldn't be deleted
    verify(message, times(0)).delete();
  }
  
  @Test
  public void testIgnoreWithoutPermissions() {
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    
    // We're not allowed to delete the message
    permissions.when(() -> PermissionUtil.checkPermission(any(), any(), any())).thenReturn(false);
  
    listener.onGuildMessageReceived(event);
  
    // Message shouldn't be deleted
    verify(message, times(0)).delete();
  }
  
  @Test
  public void testIgnoreWithoutPattern() throws SQLException {
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(guild.getIdLong()).thenReturn(1000L);
    
    // Remove pattern
    data.setBlacklist(Collections.emptyList());
    db.put(data);
  
    listener.onGuildMessageReceived(event);
  
    // Message shouldn't be deleted
    verify(message, times(0)).delete();
  }
  
  @Test
  public void testIgnoreValidMessage() {
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(guild.getIdLong()).thenReturn(1000L);
    // Check message + embeds + fields
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
    when(messageEmbed.getFields()).thenReturn(List.of(field));
    
    listener.onGuildMessageReceived(event);
  
    verify(message, times(0)).delete();
  }
  
  @Test
  public void testIgnoreOnError() throws IOException {
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(guild.getIdLong()).thenReturn(1000L);
    
    // SQLError b/c database no longer exists
    Files.deleteIfExists(SqlQuery.ENTITY_DB_PATH);
    
    listener.onGuildMessageReceived(event);
    
    verify(message, times(0)).delete();
  }
  
  @Test
  public void testDeleteWithCachedPattern() {
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(guild.getIdLong()).thenReturn(1000L);
    when(message.delete()).thenReturn(restAction);
    // Banned word
    when(message.getContentRaw()).thenReturn("banana");
  
    patternCache.put(data.getId(), Pattern.compile("banana"));
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
  
  @Test
  public void testDeleteByTextContent() {
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(guild.getIdLong()).thenReturn(1000L);
    when(message.delete()).thenReturn(restAction);
    // Banned word
    when(message.getContentRaw()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
    
    verify(message, times(1)).delete();
  }
  
  @Test
  public void testDeleteByEmbedTitle() {
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(guild.getIdLong()).thenReturn(1000L);
    when(message.delete()).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
    // Banned word
    when(messageEmbed.getTitle()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
  
  @Test
  public void testDeleteByEmbedUrl() {
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(guild.getIdLong()).thenReturn(1000L);
    when(message.delete()).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
    // Banned word
    when(messageEmbed.getUrl()).thenReturn("www.banana.com");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
  
  @Test
  public void testDeleteByEmbedDescription() {
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(guild.getIdLong()).thenReturn(1000L);
    when(message.delete()).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
    // Banned word
    when(messageEmbed.getDescription()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
  
  @Test
  public void testDeleteByEmbedFieldName() {
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(guild.getIdLong()).thenReturn(1000L);
    when(message.delete()).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
    when(messageEmbed.getFields()).thenReturn(List.of(field));
    // Banned word
    when(field.getName()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
  
  @Test
  public void testDeleteByEmbedFieldValue() {
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(guild.getIdLong()).thenReturn(1000L);
    when(message.delete()).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
    when(messageEmbed.getFields()).thenReturn(List.of(field));
    // Banned word
    when(field.getValue()).thenReturn("banana");
    
    listener.onGuildMessageReceived(event);
    
    verify(message, times(1)).delete();
  }
  
  @Test
  public void testDeleteByFooterText() {
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(guild.getIdLong()).thenReturn(1000L);
    when(message.delete()).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
    when(messageEmbed.getFooter()).thenReturn(footer);
    // Banned word
    when(footer.getText()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
}
