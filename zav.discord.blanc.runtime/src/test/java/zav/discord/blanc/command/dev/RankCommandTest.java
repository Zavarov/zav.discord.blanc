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

package zav.discord.blanc.command.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.Rank;
import zav.discord.blanc.command.AbstractDevCommandTest;
import zav.discord.blanc.databind.UserDto;
import zav.discord.blanc.db.UserDatabase;
import zav.discord.blanc.runtime.command.dev.RankCommand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RankCommandTest extends AbstractDevCommandTest {
  @BeforeEach
  public void setUp() {
    command = parse("b:dev.rank %s root", selfUserId);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(RankCommand.class);
  }
  
  @Test
  public void testGrantRank() throws Exception {
    userDto.getRanks().add(Rank.ROOT.name());
    
    command.run();
  
    ArgumentCaptor<String> msgCaptorCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> rankCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);
  
    verify(channelView, times(1)).send(msgCaptorCaptor.capture(), rankCaptor.capture(), userCaptor.capture());
  
    // Correct message?
    assertThat(msgCaptorCaptor.getValue()).isEqualTo("Granted rank \"%s\" to %s.");
    assertThat(rankCaptor.getValue()).isEqualTo("ROOT");
    assertThat(userCaptor.getValue()).isEqualTo(selfUserName);
    
    // Has the rank been added?
    assertThat(selfUserDto.getRanks()).contains(Rank.ROOT.name());
    
    // Has the database been updated?
    UserDto dbUser = UserDatabase.get(selfUserId);
    assertThat(dbUser.getRanks()).contains(Rank.ROOT.name());
    assertThat(dbUser.getDiscriminator()).isEqualTo(selfUserDiscriminator);
    assertThat(dbUser.getId()).isEqualTo(selfUserId);
    assertThat(dbUser.getName()).isEqualTo(selfUserName);
  }
  
  @Test
  public void testRemoveRank() throws Exception {
    userDto.getRanks().add(Rank.ROOT.name());
    selfUserDto.getRanks().add(Rank.ROOT.name());
  
    command.run();
  
    ArgumentCaptor<String> msgCaptorCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> rankCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);
  
    verify(channelView, times(1)).send(msgCaptorCaptor.capture(), rankCaptor.capture(), userCaptor.capture());
  
    // Correct message?
    assertThat(msgCaptorCaptor.getValue()).isEqualTo("Removed rank \"%s\" from %s.");
    assertThat(rankCaptor.getValue()).isEqualTo("ROOT");
    assertThat(userCaptor.getValue()).isEqualTo(selfUserName);
  
    // Has the rank been removed?
    assertThat(selfUserDto.getRanks()).containsExactly(Rank.USER.name());
  
    // Has the database been updated?
    UserDto dbUser = UserDatabase.get(selfUserId);
    assertThat(dbUser.getRanks()).containsExactly(Rank.USER.name());
    assertThat(dbUser.getDiscriminator()).isEqualTo(selfUserDiscriminator);
    assertThat(dbUser.getId()).isEqualTo(selfUserId);
    assertThat(dbUser.getName()).isEqualTo(selfUserName);
  }
  
  @Test
  public void testInsufficientRank() throws Exception {
    command.run();
  
    ArgumentCaptor<String> msgCaptorCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> rankCaptor = ArgumentCaptor.forClass(String.class);
  
    verify(channelView, times(1)).send(msgCaptorCaptor.capture(), rankCaptor.capture());
    
    // Correct message?
    assertThat(msgCaptorCaptor.getValue()).isEqualTo("You lack the rank to grant the \"%s\" Rank");
    assertThat(rankCaptor.getValue()).isEqualTo("ROOT");
  }
}
