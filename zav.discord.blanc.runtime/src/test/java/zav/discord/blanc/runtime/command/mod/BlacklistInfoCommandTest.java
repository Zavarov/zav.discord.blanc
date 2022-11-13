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

package zav.discord.blanc.runtime.command.mod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;
import net.dv8tion.jda.api.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Checks whether the interactive configuration message is returned.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BlacklistInfoCommandTest extends AbstractTest {
  @Captor ArgumentCaptor<List<Site.Page>> captor;
  
  GuildCommandManager manager;
  BlacklistInfoCommand command;
  
  /**
   * Initializes the command with no arguments.
   */
  @BeforeEach
  public void setUp() {
    when(event.getMember()).thenReturn(member);
    when(event.getGuild()).thenReturn(guild);
    
    manager = spy(new GuildCommandManager(shard, event));
    command = new BlacklistInfoCommand(event, manager);
    
    doNothing().when(manager).submit(captor.capture(), anyString());
  }
  
  @Test
  public void testShowEmptyPage() throws Exception {
    guildEntity.setBlacklist(Collections.emptyList());
    
    command.run();
  
    assertTrue(captor.getValue().isEmpty());
  }
  
  /**
   * Use Case: The list of banned expressions should be displayed correctly. The command should be
   * able to handle a single but also multiple expressions.
   *
   * @param blacklist A list of banned expressions.
   */
  @MethodSource
  @ParameterizedTest
  public void testShowPage(List<String> blacklist) {
    guildEntity.setBlacklist(blacklist);
    
    command.run();
  
    assertEquals(captor.getValue().size(), 1);
  }
  
  /**
   * Argument provider for {@link #testShowPage(List)}.
   *
   * @return The arguments used to call {@link #testShowPage(List)}.
   */
  public static Stream<Arguments> testShowPage() {
    return Stream.of(
        Arguments.of(List.of("bananaphone")),
        Arguments.of(List.of("foo", "bar"))
    );
  }
  
  @Test
  public void testGetPermissions() {
    assertEquals(command.getPermissions(), EnumSet.of(Permission.MESSAGE_MANAGE));
  }
}
