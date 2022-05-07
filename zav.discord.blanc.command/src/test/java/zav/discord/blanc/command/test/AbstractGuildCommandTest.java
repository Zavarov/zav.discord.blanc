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

package zav.discord.blanc.command.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.EnumSet;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.guice.GuildCommandModule;
import zav.discord.blanc.command.InsufficientPermissionException;
import zav.discord.blanc.command.InsufficientRankException;

/**
 * Checks whether guild commands fail if a user with insufficient rank or permission tries to
 * execute them.
 */
@ExtendWith(MockitoExtension.class)
public class AbstractGuildCommandTest {
  Injector injector;
  @Mock Member member;
  @Mock User user;
  
  /**
   * Initializes the injector used for instantiating guild commands.
   */
  @BeforeEach
  public void setUp() {
    SlashCommandEvent event = mock(SlashCommandEvent.class);
    when(event.getJDA()).thenReturn(mock(JDA.class));
    when(event.getChannel()).thenReturn(mock(MessageChannel.class));
    when(event.getUser()).thenReturn(user);
    when(event.getGuild()).thenReturn(mock(Guild.class));
    when(event.getTextChannel()).thenReturn(mock(TextChannel.class));
    when(event.getMember()).thenReturn(member);
    
    injector = Guice.createInjector(new GuildCommandModule(event));
  }
  
  @Test
  public void testValidate() throws Exception {
    Command command = injector.getInstance(GuildCommand.class);
  
    when(member.getPermissions(any())).thenReturn(EnumSet.allOf(Permission.class));
    when(member.getUser()).thenReturn(user);
    
    command.validate();
  }
  
  @Test
  public void testValidateWithInsufficientRank() {
    Command command = injector.getInstance(DeveloperCommand.class);
    
    assertThrows(InsufficientRankException.class, command::validate);
  }
  
  @Test
  public void testValidateWithInsufficientPermission() {
    Command command = injector.getInstance(ModeratorCommand.class);
  
    when(member.getPermissions(any())).thenReturn(EnumSet.noneOf(Permission.class));
    when(member.getUser()).thenReturn(user);
  
    assertThrows(InsufficientPermissionException.class, command::validate);
  }
}
