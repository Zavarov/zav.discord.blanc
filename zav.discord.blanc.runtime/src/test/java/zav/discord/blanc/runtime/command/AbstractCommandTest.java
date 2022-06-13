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

package zav.discord.blanc.runtime.command;

import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import zav.discord.blanc.api.guice.PrivateCommandModule;

public abstract class AbstractCommandTest extends AbstractTest {
  
  protected @Mock JDA jda;
  protected @Mock PrivateChannel privateChannel;
  protected @Mock User user;
  protected @Mock SlashCommandEvent event;
  protected @Mock ReplyAction reply;
  
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    
    when(event.getJDA()).thenReturn(jda);
    when(event.getChannel()).thenReturn(privateChannel);
    when(event.getUser()).thenReturn(user);
    when(event.getPrivateChannel()).thenReturn(privateChannel);
    injector = Guice.createInjector(new PrivateCommandModule(event), new TestModule());
  }
}
