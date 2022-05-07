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

package zav.discord.blanc.api.internal.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.cache.Cache;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import zav.discord.blanc.api.internal.BlacklistListener;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.db.GuildTable;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Test class for checking whether forbidden message are deleted automatically.
 */
@ExtendWith(MockitoExtension.class)
public class BlacklistListenerTest {
  MockedStatic<PermissionUtil> permissions;

  BlacklistListener listener;
  @Mock Cache<Guild, Pattern> patternCache;
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
  @Mock GuildTable db;
  @Mock GuildEntity guildEntity;
  
  /**
   * Initializes a fictitious GuildMessageReceivedEvent.
   */
  @BeforeEach
  public void setUp() throws Exception {
    permissions = mockStatic(PermissionUtil.class);
    permissions.when(() -> PermissionUtil.checkPermission(any(), any(), any())).thenReturn(true);
    
    listener = new BlacklistListener();
    listener.setDatabase(db);
    listener.setPatternCache(patternCache);
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
  
  /**
   * Use Case: Messages by this program should never be deleted. E.g. when displaying the list of
   * all banned expressions.
   */
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
  
  /**
   * Use Case: All messages are accepted when the program lacks permissions to delete messages.
   */
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
  
  /**
   * Use Case: All messages are accepted without banned expressions.
   *
   * @throws SQLException When a database error occurred.
   */
  @Test
  public void testIgnoreWithoutPattern() throws SQLException {
    when(db.get(any(Guild.class))).thenReturn(Optional.of(guildEntity));
    when(guildEntity.getBlacklist()).thenReturn(Collections.emptyList());
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
  
    listener.onGuildMessageReceived(event);
  
    // Message shouldn't be deleted
    verify(message, times(0)).delete();
  }
  
  /**
   * Use Case: Only messages containing banned expressions should be deleted.
   *
   * @throws SQLException When a database error occurred.
   */
  @Test
  public void testIgnoreValidMessage() throws SQLException {
    when(db.get(any(Guild.class))).thenReturn(Optional.of(guildEntity));
    when(guildEntity.getBlacklist()).thenReturn(List.of("banana"));
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    // Check message + embeds + fields
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
    when(messageEmbed.getFields()).thenReturn(List.of(field));
    
    listener.onGuildMessageReceived(event);
  
    verify(message, times(0)).delete();
  }
  
  /**
   * Use Case: Messages shouldn't be deleted when an internal error occurred.
   *
   * @throws SQLException When a database error occurred.
   */
  @Test
  public void testIgnoreOnError() throws SQLException {
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(db.get(any(Guild.class))).thenThrow(SQLException.class);
    
    listener.onGuildMessageReceived(event);
    
    verify(message, times(0)).delete();
  }
  
  /**
   * Use Case: Use the pattern that has been stored in the local cache when validating messages.
   */
  @Test
  public void testDeleteWithCachedPattern() {
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(message.delete()).thenReturn(restAction);
    doReturn(Pattern.compile("banana")).when(patternCache).getIfPresent(any(Guild.class));
    // Banned word
    when(message.getContentRaw()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
  
  /**
   * Use Case: Text messages with banned expressions in their body should be deleted.
   *
   * @throws SQLException When a database error occurred.
   */
  @Test
  public void testDeleteByTextContent() throws SQLException {
    when(db.get(any(Guild.class))).thenReturn(Optional.of(guildEntity));
    when(guildEntity.getBlacklist()).thenReturn(List.of("banana"));
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(message.delete()).thenReturn(restAction);
    // Banned word
    when(message.getContentRaw()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
    
    verify(message, times(1)).delete();
  }
  
  /**
   * Use Case: Embedded messages with banned expressions in their title should be deleted.
   *
   * @throws SQLException When a database error occurred.
   */
  @Test
  public void testDeleteByEmbedTitle() throws SQLException {
    when(db.get(any(Guild.class))).thenReturn(Optional.of(guildEntity));
    when(guildEntity.getBlacklist()).thenReturn(List.of("banana"));
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(message.delete()).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
    // Banned word
    when(messageEmbed.getTitle()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
  
  /**
   * Use Case: Embedded messages with banned expressions in their URL should be deleted.
   *
   * @throws SQLException When a database error occurred.
   */
  @Test
  public void testDeleteByEmbedUrl() throws SQLException {
    when(db.get(any(Guild.class))).thenReturn(Optional.of(guildEntity));
    when(guildEntity.getBlacklist()).thenReturn(List.of("banana"));
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(message.delete()).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
    // Banned word
    when(messageEmbed.getUrl()).thenReturn("www.banana.com");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
  
  /**
   * Use Case: Embedded messages with banned expressions in their description should be deleted.
   *
   * @throws SQLException When a database error occurred.
   */
  @Test
  public void testDeleteByEmbedDescription() throws SQLException {
    when(db.get(any(Guild.class))).thenReturn(Optional.of(guildEntity));
    when(guildEntity.getBlacklist()).thenReturn(List.of("banana"));
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(message.delete()).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
    // Banned word
    when(messageEmbed.getDescription()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
  
  /**
   * Use Case: Embedded messages with banned expressions in their field names should be deleted.
   *
   * @throws SQLException When a database error occurred.
   */
  @Test
  public void testDeleteByEmbedFieldName() throws SQLException {
    when(db.get(any(Guild.class))).thenReturn(Optional.of(guildEntity));
    when(guildEntity.getBlacklist()).thenReturn(List.of("banana"));
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(message.delete()).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
    when(messageEmbed.getFields()).thenReturn(List.of(field));
    // Banned word
    when(field.getName()).thenReturn("banana");
  
    listener.onGuildMessageReceived(event);
  
    verify(message, times(1)).delete();
  }
  
  /**
   * Use Case: Embedded messages with banned expressions in their field values should be deleted.
   *
   * @throws SQLException When a database error occurred.
   */
  @Test
  public void testDeleteByEmbedFieldValue() throws SQLException {
    when(db.get(any(Guild.class))).thenReturn(Optional.of(guildEntity));
    when(guildEntity.getBlacklist()).thenReturn(List.of("banana"));
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
    when(message.delete()).thenReturn(restAction);
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
    when(messageEmbed.getFields()).thenReturn(List.of(field));
    // Banned word
    when(field.getValue()).thenReturn("banana");
    
    listener.onGuildMessageReceived(event);
    
    verify(message, times(1)).delete();
  }
  
  /**
   * Use Case: Embedded messages with banned expressions in their footer should be deleted.
   *
   * @throws SQLException When a database error occurred.
   */
  @Test
  public void testDeleteByFooterText() throws SQLException {
    when(db.get(any(Guild.class))).thenReturn(Optional.of(guildEntity));
    when(guildEntity.getBlacklist()).thenReturn(List.of("banana"));
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(author.getJDA()).thenReturn(jda);
    when(jda.getSelfUser()).thenReturn(selfUser);
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
