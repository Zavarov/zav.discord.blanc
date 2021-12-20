/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.jda.internal.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.concurrent.ExecutorService;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.command.Commands;
import zav.discord.blanc.command.parser.IntermediateCommand;
import zav.discord.blanc.command.parser.Parser;
import zav.discord.blanc.databind.MessageValueObject;
import zav.discord.blanc.jda.AbstractTest;

public class GuildCommandListenerTest extends AbstractTest {
  private static final String commandName = "test";
  
  GuildCommandListener listener;
  GuildMessageReceivedEvent event;
  Shard shard;
  
  @BeforeAll
  public static void setUpAll() {
    Commands.bind(commandName, TestCommand.class);
  }
  
  @AfterAll
  public static void tearDownAll() {
    Commands.clear();
  }
  
  @BeforeEach
  public void setUp() {
    shard = mock(Shard.class);
    listener = new GuildCommandListener(shard);
    
    Injector injector = Guice.createInjector(new TestModule());
    injector.injectMembers(listener);
    
    event = new GuildMessageReceivedEvent(jda, -1, jdaMessage);
  }
  
  
  @Test
  public void testOnGuildMessageReceived() {
    listener.onGuildMessageReceived(event);
    
    verify(shard, times(1)).submit(any());
  }
  
  private static class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      Parser parser = mock(Parser.class);
      IntermediateCommand command = mock(IntermediateCommand.class);
      
      when(command.getName()).thenReturn(commandName);
      when(parser.parse(any(MessageValueObject.class))).thenReturn(command);
      
      bind(Parser.class).toInstance(parser);
      bind(ExecutorService.class).toInstance(mock(ExecutorService.class));
    }
  }
  
  private static class TestCommand extends AbstractCommand {
    @Override
    public void run() throws Exception {}
  }
}
