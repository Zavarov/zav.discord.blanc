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

package zav.discord.blanc.runtime.command.dev;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import java.util.List;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.command.CommandManager;
import zav.discord.blanc.databind.Rank;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.runtime.command.AbstractDatabaseTest;

/**
 * Check whether developer can request and relinquish super-user privileges.
 */
@ExtendWith(MockitoExtension.class)
public class FailsafeCommandTest extends AbstractDatabaseTest<UserEntity> {
  
  @Mock SlashCommandEvent event;
  @Mock ReplyAction reply;
  @Mock User user;
  CommandManager manager;
  FailsafeCommand command;
  
  /**
   * Initializes the command with no arguments.
   */
  @BeforeEach
  public void setUp() {
    setUp(new UserEntity());
    when(event.getUser()).thenReturn(user);
    when(event.replyFormat(anyString(), nullable(String.class))).thenReturn(reply);
    when(entityManager.find(eq(UserEntity.class), any())).thenReturn(entity);
    
    manager = new CommandManager(client, event);
    command = new FailsafeCommand(event, manager);
  }
  
  
  @Test
  public void testBecomeRoot() throws Exception {
    entity.setRanks(Lists.newArrayList(Rank.DEVELOPER));
    
    command.run();
    
    assertEquals(entity.getRanks(), List.of(Rank.ROOT));
  }
  
  @Test
  public void testBecomeDeveloper() throws Exception {
    entity.setRanks(Lists.newArrayList(Rank.ROOT));
    
    command.run();
    
    assertEquals(entity.getRanks(), List.of(Rank.DEVELOPER));
  }
}
