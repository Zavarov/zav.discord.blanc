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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Checks whether it is possible to blacklist/whitelist regular expressions.
 */
@ExtendWith(MockitoExtension.class)
public class BlacklistAddCommandTest extends AbstractTest {
  
  @Mock OptionMapping pattern;
  
  GuildCommandManager manager;
  BlacklistAddCommand command;

  /**
   * Initializes the command with argument {@code foo}.
   */
  @BeforeEach
  public void setUp() {
    manager = new GuildCommandManager(shard, event);
    command = new BlacklistAddCommand(event, manager);
  }

  /**
   * Tests whether the expression has been blacklisted.
   */
  @Test
  public void testAddExpression() throws Exception {
    when(event.getOption(anyString())).thenReturn(pattern);
    when(pattern.getAsString()).thenReturn("foo");
    
    guildEntity.setBlacklist(new ArrayList<>());
    
    command.run();
    
    assertEquals(guildEntity.getBlacklist(), List.of("foo"));
    verify(patternCache).invalidate(guild);
  }
  
  /**
   * Tests whether the same expression can't be blacklisted twice.
   */
  @Test
  public void testAlreadyAddedExpression() throws Exception {
    when(event.getOption(anyString())).thenReturn(pattern);
    when(pattern.getAsString()).thenReturn("foo");
    
    guildEntity.setBlacklist(new ArrayList<>(List.of("foo")));
    
    command.run();
    
    assertEquals(guildEntity.getBlacklist(), List.of("foo"));
    verify(patternCache, times(0)).invalidate(guild);
  }
  
  @Test
  public void testGetPermissions() {
    assertEquals(command.getPermissions(), EnumSet.of(Permission.MESSAGE_MANAGE));
  }
}
