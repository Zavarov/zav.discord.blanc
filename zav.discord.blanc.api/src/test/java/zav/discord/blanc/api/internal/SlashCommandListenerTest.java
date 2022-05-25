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

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.Commands;

/**
 * Test Case for checking whether commands are executed when triggered via slash commands.
 */
@ExtendWith(MockitoExtension.class)
public class SlashCommandListenerTest {
  static final String name = "name";
  
  @Mock Command command;
  @Mock User author;
  @Mock SlashCommandEvent event;
  @Mock Injector injector;
  @Mock ReplyAction reply;
  @Mock ScheduledExecutorService queue;
  
  SlashCommandListener listener;
  
  /**
   * Initializes the slash command listener and registers a single command with name {@link #name}.
   */
  @BeforeEach
  public void setUp() {
    listener = new SlashCommandListener(queue, injector);
    
    Commands.bind(name, Command.class);
  }
  
  @AfterEach
  public void tearDown() {
    Commands.clear();
  }
  
  /**
   * Use Case: Only accept messages which have been sent by a user account.
   */
  @Test
  public void testIgnoreBotMessages() {
    when(event.getUser()).thenReturn(author);
    when(author.isBot()).thenReturn(true);
    
    listener.onSlashCommand(event);
    
    verifyNoInteractions(queue);
  }
  
  /**
   * Use Case: Ignore unregistered commands.
   */
  @Test
  public void testIgnoreInvalidCommand() {
    when(event.getUser()).thenReturn(author);
    
    listener.onSlashCommand(event);
    
    verifyNoInteractions(queue);
  }
  
  /**
   * Use Case: Execute commands from within a guild.
   *
   * @throws Exception When an error occurred during command execution.
   */
  @Test
  public void testExecuteGuildCommand() throws Exception {
    when(event.getUser()).thenReturn(author);
    when(event.getName()).thenReturn(name);
    when(event.isFromGuild()).thenReturn(true);
    
    when(injector.createChildInjector(any(Module.class))).thenReturn(injector);
    when(injector.getInstance(Command.class)).thenReturn(command);
    
    doAnswer(invocation -> {
      Runnable job = invocation.getArgument(0);
      job.run();
      return null;
    }).when(queue).submit(any(Runnable.class));
    
    listener.onSlashCommand(event);
    
    verify(command, times(1)).postConstruct();
    verify(command, times(1)).validate();
    verify(command, times(1)).run();
  }
  
  /**
   * Use Case: Execute commands from within a guild, but an internal error occurred.
   *
   * @throws Exception When an error occurred during command execution.
   */
  @ParameterizedTest
  @MethodSource("contentProvider")
  public void testExecuteGuildCommandWithError(String message, Exception cause) throws Exception {
    when(event.getUser()).thenReturn(author);
    when(event.getName()).thenReturn(name);
    when(event.isFromGuild()).thenReturn(true);
    when(event.replyEmbeds(any(MessageEmbed.class))).thenReturn(reply);
  
    when(injector.createChildInjector(any(Module.class))).thenReturn(injector);
    when(injector.getInstance(Command.class)).thenReturn(command);
    
    doThrow(new Exception(message, cause)).when(command).run();
    
    doAnswer(invocation -> {
      Runnable job = invocation.getArgument(0);
      job.run();
      return null;
    }).when(queue).submit(any(Runnable.class));
    
    listener.onSlashCommand(event);
    
    verify(event, times(1)).replyEmbeds(any(MessageEmbed.class));
  }
  
  /**
   * Use Case: Execute commands from within a private channel.
   *
   * @throws Exception When an error occurred during command execution.
   */
  @Test
  public void testExecutePrivateCommand() throws Exception {
    when(event.getUser()).thenReturn(author);
    when(event.getName()).thenReturn(name);
  
    when(injector.createChildInjector(any(Module.class))).thenReturn(injector);
    when(injector.getInstance(Command.class)).thenReturn(command);
    
    doAnswer(invocation -> {
      Runnable job = invocation.getArgument(0);
      job.run();
      return null;
    }).when(queue).submit(any(Runnable.class));
    
    listener.onSlashCommand(event);
    
    verify(command, times(1)).postConstruct();
    verify(command, times(1)).validate();
    verify(command, times(1)).run();
  }
  
  /**
   * Use Case: Execute commands from within a private channel, but an internal error occurred.
   *
   * @throws Exception When an error occurred during command execution.
   */
  @ParameterizedTest
  @MethodSource("contentProvider")
  public void testExecutePrivateCommandWithError(String message, Exception cause) throws Exception {
    when(event.getUser()).thenReturn(author);
    when(event.getName()).thenReturn(name);
    when(event.replyEmbeds(any(MessageEmbed.class))).thenReturn(reply);
  
    when(injector.createChildInjector(any(Module.class))).thenReturn(injector);
    when(injector.getInstance(Command.class)).thenReturn(command);
    
    doThrow(new Exception(message, cause)).when(command).run();
    
    doAnswer(invocation -> {
      Runnable job = invocation.getArgument(0);
      job.run();
      return null;
    }).when(queue).submit(any(Runnable.class));
    
    listener.onSlashCommand(event);
  
    verify(event, times(1)).replyEmbeds(any(MessageEmbed.class));
  }
  
  static Stream<Arguments> contentProvider() {
    return Stream.of(
          arguments("message", new Exception()),
          arguments(null, new Exception()),
          arguments("message", null),
          arguments(null, null)
    );
  }
}
