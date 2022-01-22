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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.Parser;

/**
 * Checks whether private messages are able to produce (private) commands.
 */
public class PrivateCommandListenerTest {
  
  final long responseNumber = 11111L;
  
  @Mock Parser parser;
  @Mock ScheduledExecutorService queue;
  @Mock Command command;
  
  @Mock JDA jda;
  @Mock Message message;
  @Mock PrivateChannel privateChannel;
  @Mock User author;
  @Mock MessageAction action;
  
  PrivateMessageReceivedEvent event;
  PrivateCommandListener listener;
  AutoCloseable closeable;
  
  /**
   * Creates a private command listener with a valid private message received event.
   */
  @BeforeEach
  public void setUp() {
    closeable = openMocks(this);
    
    doAnswer(invocation -> {
      Runnable job = invocation.getArgument(0);
      job.run();
      return null;
    }).when(queue).submit(any(Runnable.class));
  
    when(parser.parse(any(PrivateMessageReceivedEvent.class))).thenReturn(Optional.of(command));
    when(message.getPrivateChannel()).thenReturn(privateChannel);
    when(message.getAuthor()).thenReturn(author);
    when(privateChannel.sendMessageEmbeds(any(MessageEmbed.class))).thenReturn(action);
    
    Injector injector = Guice.createInjector(new TestModule());
    listener = injector.getInstance(PrivateCommandListener.class);
  
    event = new PrivateMessageReceivedEvent(jda, responseNumber, message);
  }
  
  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }
  
  @Test
  public void testExecuteCommand() throws Exception {
    listener.onPrivateMessageReceived(event);
    
    verify(command, times(1)).postConstruct();
    verify(command, times(1)).validate();
    verify(command, times(1)).run();
  }
  
  @Test
  public void testExecuteCommandWithError() throws Exception {
    String message = "message";
    Throwable cause = new Exception();
    doThrow(new Exception(message, cause)).when(command).run();
  
    listener.onPrivateMessageReceived(event);
  
    verify(privateChannel, times(1)).sendMessageEmbeds(any(MessageEmbed.class));
  }
  
  @Test
  public void testIgnoreBotMessages() {
    // Bot message -> ignore
    when(author.isBot()).thenReturn(true);
  
    listener.onPrivateMessageReceived(event);
  
    verifyNoInteractions(queue);
  }
  
  @Test
  public void testIgnoreInvalidCommands() {
    // Message not a command
    when(parser.parse(any(PrivateMessageReceivedEvent.class))).thenReturn(Optional.empty());
  
    listener.onPrivateMessageReceived(event);
  
    verifyNoInteractions(queue);
  }
  
  private class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(Parser.class).toInstance(parser);
      bind(ScheduledExecutorService.class).toInstance(queue);
    }
  }
}
