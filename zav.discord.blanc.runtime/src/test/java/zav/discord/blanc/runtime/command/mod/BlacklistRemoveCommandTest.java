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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Checks whether it is possible to blacklist/whitelist regular expressions.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BlacklistRemoveCommandTest extends AbstractTest {
  
  @Mock OptionMapping pattern;
  @Mock OptionMapping index;
  GuildCommandManager manager;
  BlacklistRemoveCommand command;
  
  /**
   * Initializes the command with argument {@code foo}.
   */
  @BeforeEach
  public void setUp() {
    when(event.getMember()).thenReturn(member);
    when(event.getGuild()).thenReturn(guild);
    
    manager = new GuildCommandManager(client, event);
    command = new BlacklistRemoveCommand(event, manager);

    guildEntity.setBlacklist(new ArrayList<>(List.of("foo")));
  }
  
  /**
   * Tests whether the expression has been whitelisted, given its name.
   */
  @Test
  public void testRemoveByName() throws Exception {
    when(event.getOption("pattern")).thenReturn(pattern);
    when(event.getOption("index")).thenReturn(null);
    when(pattern.getAsString()).thenReturn("foo");
    
    command.run();
    
    assertEquals(guildEntity.getBlacklist(), Collections.emptyList());
    verify(patternCache).invalidate(guild);
  }
  
  @Test
  public void testRemoveByUnknownName() {
    when(event.getOption("pattern")).thenReturn(pattern);
    when(event.getOption("index")).thenReturn(null);
    when(pattern.getAsString()).thenReturn("bar");
    
    command.run();
    
    assertEquals(guildEntity.getBlacklist(), List.of("foo"));
    verify(patternCache, times(0)).invalidate(guild);
  }
  
  /**
   * Tests whether the expression has been whitelisted, given its index.
   */
  @Test
  public void testRemoveByIndex() throws Exception {
    when(event.getOption("pattern")).thenReturn(null);
    when(event.getOption("index")).thenReturn(index);
    when(index.getAsLong()).thenReturn(0L);
    
    command.run();
    
    assertEquals(guildEntity.getBlacklist(), Collections.emptyList());
    verify(patternCache).invalidate(guild);
  }
  
  /**
   * Tests whether the command is ignored, if an index smaller than zero is given..
   */
  @Test
  public void testIndexTooLow() throws Exception {
    when(event.getOption("pattern")).thenReturn(null);
    when(event.getOption("index")).thenReturn(index);
    when(index.getAsLong()).thenReturn(-1L);
    
    command.run();
    
    assertEquals(guildEntity.getBlacklist(), Collections.singletonList("foo"));
    verify(patternCache, times(0)).invalidate(guild);
  }
  
  /**
   * Tests whether the command is ignored, if an index larger than the number of blacklisted names
   * is given.
   */
  @ParameterizedTest
  @ValueSource(longs = {1, 2, Long.MAX_VALUE})
  public void testIndexTooHigh(long value) throws Exception {
    when(event.getOption("pattern")).thenReturn(null);
    when(event.getOption("index")).thenReturn(index);
    when(index.getAsLong()).thenReturn(value);
    
    command.run();
    
    assertEquals(guildEntity.getBlacklist(), Collections.singletonList("foo"));
    verify(patternCache, times(0)).invalidate(guild);
  }
  
  /**
   * Tests whether the command is ignored, if neither name or index are given..
   */
  @ParameterizedTest
  @ValueSource(longs = {1, 2, Long.MAX_VALUE})
  public void testInvalidArgument(long value) throws Exception {
    when(event.getOption("pattern")).thenReturn(null);
    when(event.getOption("index")).thenReturn(null);
    
    command.run();
    
    assertEquals(guildEntity.getBlacklist(), Collections.singletonList("foo"));
    verify(patternCache, times(0)).invalidate(guild);
  }
  
  @Test
  public void testGetPermissions() {
    assertEquals(command.getPermissions(), EnumSet.of(Permission.MESSAGE_MANAGE));
  }
}
