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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.cache.PatternCache;
import zav.discord.blanc.databind.GuildEntity;

/**
 * Test class for checking whether forbidden message are deleted automatically.
 */
@ExtendWith(MockitoExtension.class)
public class BlacklistListenerTest {
  MockedStatic<PermissionUtil> permissions;

  BlacklistListener listener;
  GuildEntity guildEntity;
  @Mock PatternCache patternCache;
  @Mock JDA jda;
  @Mock SelfUser selfUser;
  @Mock Message message;
  @Mock AuditableRestAction<Void> restAction;
  @Mock User author;
  @Mock Guild guild;
  @Mock TextChannel textChannel;
  @Mock GuildMessageReceivedEvent event;
  
  /**
   * Initializes a fictitious GuildMessageReceivedEvent.
   */
  @BeforeEach
  public void setUp() {
    permissions = mockStatic(PermissionUtil.class);
    permissions.when(() -> PermissionUtil.checkPermission(any(), any(), any())).thenReturn(true);
    
    guildEntity = new GuildEntity();
    listener = spy(new BlacklistListener(patternCache));
  }
  
  /**
   * Close all static mocks.
   */
  @AfterEach
  public void tearDown() {
    permissions.close();
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
    doReturn(false).when(listener).isSelfUser(any());
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    
    // We're not allowed to delete the message
    permissions.when(() -> PermissionUtil.checkPermission(any(), any(), any())).thenReturn(false);
  
    listener.onGuildMessageReceived(event);
  
    // Message shouldn't be deleted
    verify(message, times(0)).delete();
  }
  
  /**
   * Use Case: All messages are accepted without banned expressions.
   */
  @Test
  public void testIgnoreWithoutPattern() {
    doReturn(false).when(listener).isSelfUser(any());
    doReturn(true).when(listener).hasRequiredPermissions(any(), any());
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
  
    listener.onGuildMessageReceived(event);
  
    // Message shouldn't be deleted
    verify(message, times(0)).delete();
  }
  
  /**
   * Use Case: Only messages containing banned expressions should be deleted.
   */
  @Test
  public void testIgnoreValidMessage() {
    doReturn(false).when(listener).isSelfUser(any());
    doReturn(true).when(listener).hasRequiredPermissions(any(), any());
    doReturn(Optional.of(Pattern.compile("banana"))).when(patternCache).get(any(Guild.class));
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    when(message.getContentRaw()).thenReturn("hadoop");
    
    listener.onGuildMessageReceived(event);
  
    verify(message, times(0)).delete();
  }
  
  /**
   * Use Case: Text messages with banned expressions in their body should be deleted.
   */
  @ParameterizedTest
  @ValueSource(strings = {"banana", "pizza"})
  public void testDeleteMessage(String content) {
    doReturn(false).when(listener).isSelfUser(any());
    doReturn(true).when(listener).hasRequiredPermissions(any(), any());
    doReturn(Optional.of(Pattern.compile(content))).when(patternCache).get(any(Guild.class));
    
    when(event.getAuthor()).thenReturn(author);
    when(event.getGuild()).thenReturn(guild);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getMessage()).thenReturn(message);
    // Banned word
    when(message.getContentRaw()).thenReturn(content);
    when(message.delete()).thenReturn(restAction);
  
    listener.onGuildMessageReceived(event);
    
    verify(message, times(1)).delete();
  }
}
