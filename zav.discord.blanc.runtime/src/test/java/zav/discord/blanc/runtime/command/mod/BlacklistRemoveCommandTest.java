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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.runtime.command.AbstractDatabaseTest;

/**
 * Checks whether it is possible to blacklist/whitelist regular expressions.
 */
@ExtendWith(MockitoExtension.class)
public class BlacklistRemoveCommandTest extends AbstractDatabaseTest<GuildEntity> {
  
  @Mock OptionMapping pattern;
  @Mock OptionMapping index;
  GuildCommandManager manager;
  BlacklistRemoveCommand command;
  
  /**
   * Initializes the command with argument {@code foo}.
   */
  @BeforeEach
  public void setUp() {
    super.setUp(new GuildEntity());
    when(event.getMember()).thenReturn(member);
    when(event.getGuild()).thenReturn(guild);
    when(entityManager.find(eq(GuildEntity.class), any())).thenReturn(entity);
    
    manager = new GuildCommandManager(client, event);
    command = new BlacklistRemoveCommand(event, manager);

    entity.setBlacklist(new ArrayList<>(List.of("foo")));
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
    
    assertEquals(entity.getBlacklist(), Collections.emptyList());
    verify(patternCache).invalidate(guild);
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
    
    assertEquals(entity.getBlacklist(), Collections.emptyList());
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
    
    assertEquals(entity.getBlacklist(), Collections.singletonList("foo"));
    verify(patternCache).invalidate(guild);
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
    
    assertEquals(entity.getBlacklist(), Collections.singletonList("foo"));
    verify(patternCache).invalidate(guild);
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
    
    assertEquals(entity.getBlacklist(), Collections.singletonList("foo"));
    verify(patternCache).invalidate(guild);
  }
}
