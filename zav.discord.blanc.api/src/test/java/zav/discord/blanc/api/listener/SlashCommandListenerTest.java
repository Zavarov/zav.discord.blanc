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

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.CommandParser;
import zav.discord.blanc.api.util.ValidationException;

/**
 * Test Case for checking whether commands are executed when triggered via slash commands.
 */
@ExtendWith(MockitoExtension.class)
public class SlashCommandListenerTest {
  static final String name = "name";
  
  @Mock Command command;
  @Mock User author;
  @Mock SlashCommandEvent event;
  @Mock ReplyAction reply;
  @Mock ScheduledExecutorService queue;
  @Mock CommandParser parser;
  @Mock MessageEmbed errorMessage;
  ValidationException validationError;
  
  SlashCommandListener listener;
  
  /**
   * Initializes the slash command listener and registers a single command with name {@link #name}.
   */
  @BeforeEach
  public void setUp() {
    listener = new SlashCommandListener(queue, parser);
    validationError = new TestValidationException();
    
    when(event.getUser()).thenReturn(author);
    lenient().when(event.replyEmbeds(any(MessageEmbed.class))).thenReturn(reply);
    lenient().when(parser.parse(event)).thenReturn(Optional.of(command));
    
    // Immediately execute the runnable
    lenient().doAnswer(invocation -> {
      Runnable job = invocation.getArgument(0);
      job.run();
      return null;
    }).when(queue).submit(any(Runnable.class));
  }
  
  /**
   * Use Case: Only accept messages which have been sent by a user account.
   */
  @Test
  public void testIgnoreBotMessages() {
    when(author.isBot()).thenReturn(true);
    
    listener.onSlashCommand(event);
    
    verifyNoInteractions(queue);
  }
  
  /**
   * Use Case: Ignore unregistered commands.
   */
  @Test
  public void testIgnoreInvalidCommand() {
    when(parser.parse(event)).thenReturn(Optional.empty());
    
    listener.onSlashCommand(event);
    
    verifyNoInteractions(queue);
  }
  
  /**
   * Use Case: Validation errors should contain a descriptive message instead of the stack trace.
   *
   * @throws Exception When an error occurred during command execution.
   */
  @Test
  public void testExecuteCommandWithValidationError() throws Exception {
    doThrow(validationError).when(command).validate();
    
    listener.onSlashCommand(event);
    
    verify(event, times(1)).replyEmbeds(errorMessage);
  }
  
  /**
   * Use Case: Execute a command, but an internal error occurred.
   *
   * @throws Exception When an error occurred during command execution.
   */
  @ParameterizedTest
  @MethodSource("contentProvider")
  public void testExecuteCommandWithError(String message, Exception cause) throws Exception {
    doThrow(new Exception(message, cause)).when(command).run();
    
    listener.onSlashCommand(event);
    
    verify(event, times(1)).replyEmbeds(any(MessageEmbed.class));
  }
  
  /**
   * Use Case: Execute a valid command.
   *
   * @throws Exception When an error occurred during command execution.
   */
  @Test
  public void testExecuteCommand() throws Exception {
    listener.onSlashCommand(event);
    
    verify(command, times(1)).validate();
    verify(command, times(1)).run();
  }
  
  static Stream<Arguments> contentProvider() {
    return Stream.of(
          arguments("message", new Exception()),
          arguments(null, new Exception()),
          arguments("message", null),
          arguments(null, null)
    );
  }
  
  class TestValidationException extends ValidationException {
    private static final long serialVersionUID = 1L;

    @Override
    public MessageEmbed getErrorMessage() {
      return errorMessage;
    }
  }
}
