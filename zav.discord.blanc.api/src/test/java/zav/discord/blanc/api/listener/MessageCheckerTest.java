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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test class for checking whether all variations of messages are detected by the blacklist
 * listener.
 */
public class MessageCheckerTest {
  
  Pattern pattern;
  
  @BeforeEach
  public void setUp() {
    pattern = Pattern.compile("banana");
  }
  
  @ParameterizedTest
  @MethodSource("embedProvider")
  public void testCheckEmbed(Message message, boolean shouldDelete) {
    assertEquals(BlacklistListener.MessageChecker.shouldDelete(message, pattern), shouldDelete);
  }
  
  @ParameterizedTest
  @MethodSource("contentProvider")
  public void testCheckContent(Message message, boolean shouldDelete) {
    assertEquals(BlacklistListener.MessageChecker.shouldDelete(message, pattern), shouldDelete);
  }
  
  static Stream<Arguments> embedProvider() {
    MultiValuedMap<Boolean, Message> messages = new HashSetValuedHashMap<>();
    messages.putAll(true, newMessageEmbed("babananana"));
    messages.putAll(true, newMessageEmbed("banana"));
    messages.putAll(false, newMessageEmbed("nana"));
    
    return messages.entries().stream().map(e -> Arguments.of(e.getValue(), e.getKey()));
  }
  
  static Stream<Arguments> contentProvider() {
    return Stream.of(
          Arguments.of(newMessage("banana"), true),
          Arguments.of(newMessage("babananana"), true),
          Arguments.of(newMessage("bana"), false),
          Arguments.of(newMessage("bana"), false)
    );
  }
  
  static Message newMessage(String content) {
    Message result = mock(Message.class);
    when(result.getContentRaw()).thenReturn(content);
    return result;
  }
  
  
  static Set<Message> newMessageEmbed(String content) {
    Set<Message> result = new HashSet<>();
    
    // Title
    Message message = mock(Message.class);
    MessageEmbed embed = mock(MessageEmbed.class);
    when(embed.getTitle()).thenReturn(content);
    when(message.getEmbeds()).thenReturn(List.of(embed));
    result.add(message);
  
    // Url
    message = mock(Message.class);
    embed = mock(MessageEmbed.class);
    when(embed.getUrl()).thenReturn(content);
    when(message.getEmbeds()).thenReturn(List.of(embed));
    result.add(message);
  
    // Description
    message = mock(Message.class);
    embed = mock(MessageEmbed.class);
    when(embed.getDescription()).thenReturn(content);
    when(message.getEmbeds()).thenReturn(List.of(embed));
    result.add(message);
  
    // Footer
    message = mock(Message.class);
    embed = mock(MessageEmbed.class);
    MessageEmbed.Footer footer = mock(MessageEmbed.Footer.class);
    when(footer.getText()).thenReturn(content);
    when(embed.getFooter()).thenReturn(footer);
    when(message.getEmbeds()).thenReturn(List.of(embed));
    result.add(message);
  
    message = mock(Message.class);
    embed = mock(MessageEmbed.class);
    footer = mock(MessageEmbed.Footer.class);
    when(footer.getIconUrl()).thenReturn(content);
    when(embed.getFooter()).thenReturn(footer);
    when(message.getEmbeds()).thenReturn(List.of(embed));
    result.add(message);
  
    message = mock(Message.class);
    embed = mock(MessageEmbed.class);
    footer = mock(MessageEmbed.Footer.class);
    when(footer.getProxyIconUrl()).thenReturn(content);
    when(embed.getFooter()).thenReturn(footer);
    when(message.getEmbeds()).thenReturn(List.of(embed));
    result.add(message);

    // Fields
    message = mock(Message.class);
    embed = mock(MessageEmbed.class);
    MessageEmbed.Field field = mock(MessageEmbed.Field.class);
    when(field.getName()).thenReturn(content);
    when(embed.getFields()).thenReturn(List.of(field));
    when(message.getEmbeds()).thenReturn(List.of(embed));
    result.add(message);
  
    message = mock(Message.class);
    embed = mock(MessageEmbed.class);
    field = mock(MessageEmbed.Field.class);
    when(field.getValue()).thenReturn(content);
    when(embed.getFields()).thenReturn(List.of(field));
    when(message.getEmbeds()).thenReturn(List.of(embed));
    result.add(message);
    
    return result;
  }
}
