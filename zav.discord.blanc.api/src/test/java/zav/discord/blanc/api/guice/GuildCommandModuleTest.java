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

package zav.discord.blanc.api.guice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.api.Command;

/**
 * Checks whether the injector is able to create (guild) commands.
 */
public class GuildCommandModuleTest {
  Injector injector;
  
  /**
   * Initializes an injector with a guild command module.
   */
  @BeforeEach
  public void setUp() {
    SlashCommandEvent event = mock(SlashCommandEvent.class);
    when(event.getJDA()).thenReturn(mock(JDA.class));
    when(event.getChannel()).thenReturn(mock(MessageChannel.class));
    when(event.getUser()).thenReturn(mock(User.class));
    when(event.getGuild()).thenReturn(mock(Guild.class));
    when(event.getTextChannel()).thenReturn(mock(TextChannel.class));
    when(event.getMember()).thenReturn(mock(Member.class));
  
    injector = Guice.createInjector(new GuildCommandModule(event));
  }
  
  @Test
  public void testCreateCommand() {
    injector.getInstance(TestCommand.class);
  }
  
  private static class TestCommand implements Command {
    @Override
    public void run() {}
  
    @Override
    public void validate() {}
  }
}
