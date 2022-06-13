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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.UserTable;
import zav.test.io.JsonUtils;

/**
 * Check whether developer can request and relinquish super-user privileges.
 */
@ExtendWith(MockitoExtension.class)
public class FailsafeCommandTest {
  
  @Mock SlashCommandEvent event;
  @Mock ReplyAction reply;
  @Mock UserTable db;
  @Mock User user;
  
  UserEntity entity;
  FailsafeCommand command;
  
  @BeforeEach
  public void setUp() {
    entity = JsonUtils.read("User.json", UserEntity.class);
  }
  
  
  @Test
  public void testBecomeRoot() throws Exception {
    when(db.get(user)).thenReturn(Optional.of(entity));
    entity.setRanks(Lists.newArrayList(Rank.DEVELOPER.name()));
  
    when(user.getName()).thenReturn(entity.getName());
    when(user.getDiscriminator()).thenReturn(entity.getDiscriminator());
    when(user.getAsMention()).thenReturn(entity.getName());
    when(event.replyFormat(anyString(), anyString())).thenReturn(reply);
    
    command = new FailsafeCommand(event, db, user);
    command.run();
    
    assertThat(entity.getRanks()).contains(Rank.ROOT.name());
  }
  
  @Test
  public void testBecomeDeveloper() throws Exception {
    when(db.get(user)).thenReturn(Optional.of(entity));
    entity.setRanks(Lists.newArrayList(Rank.ROOT.name()));
  
    when(user.getName()).thenReturn(entity.getName());
    when(user.getDiscriminator()).thenReturn(entity.getDiscriminator());
    when(user.getAsMention()).thenReturn(entity.getName());
    when(event.replyFormat(anyString(), anyString())).thenReturn(reply);
  
    command = new FailsafeCommand(event, db, user);
    command.run();
  
    assertThat(entity.getRanks()).contains(Rank.DEVELOPER.name());
  }
}
