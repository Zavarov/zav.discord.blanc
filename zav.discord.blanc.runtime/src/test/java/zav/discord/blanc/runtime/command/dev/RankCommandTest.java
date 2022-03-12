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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static zav.test.io.JsonUtils.read;

import java.util.List;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.UserTable;
import zav.discord.blanc.runtime.command.AbstractDevCommandTest;

/**
 * Check whether ranks can be granted and removed.
 */
@ExtendWith(MockitoExtension.class)
public class RankCommandTest extends AbstractDevCommandTest {
  private UserTable userTable;
  private UserEntity userEntity;
  
  private @Mock MessageAction action;
  
  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    
    userEntity = read("User.json", UserEntity.class);
    userTable = injector.getInstance(UserTable.class);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    check("b:dev.rank root", RankCommand.class);
  }
  
  @Test
  public void testGrantRank() throws Exception {
    userEntity.setRanks(List.of(Rank.ROOT.name()));
    userTable.put(userEntity);
    
    when(author.getIdLong()).thenReturn(userEntity.getId());
    when(author.getName()).thenReturn(userEntity.getName());
    when(author.getDiscriminator()).thenReturn(userEntity.getDiscriminator());
    when(textChannel.sendMessageFormat(any(), any(), any())).thenReturn(action);
    
    run("b:dev.rank developer");
    
    // Has the database been updated?
    UserEntity response = super.get(userTable, userEntity.getId());
    assertThat(response.getRanks()).contains(Rank.DEVELOPER.name());
    assertThat(response.getDiscriminator()).isEqualTo(userEntity.getDiscriminator());
    assertThat(response.getId()).isEqualTo(userEntity.getId());
    assertThat(response.getName()).isEqualTo(userEntity.getName());
  }
  
  @Test
  public void testRemoveRank() throws Exception {
    userEntity.setRanks(List.of(Rank.ROOT.name()));
    userTable.put(userEntity);
  
    when(author.getIdLong()).thenReturn(userEntity.getId());
    when(author.getName()).thenReturn(userEntity.getName());
    when(author.getDiscriminator()).thenReturn(userEntity.getDiscriminator());
    when(textChannel.sendMessageFormat(any(), any(), any())).thenReturn(action);
  
    run("b:dev.rank root");
  
    // Has the database been updated?
    UserEntity response = super.get(userTable, userEntity.getId());
    assertThat(response.getRanks()).isEmpty();
    assertThat(response.getDiscriminator()).isEqualTo(userEntity.getDiscriminator());
    assertThat(response.getId()).isEqualTo(userEntity.getId());
    assertThat(response.getName()).isEqualTo(userEntity.getName());
  }
  
  @Test
  public void testInsufficientRank() throws Exception {
    userTable.put(userEntity);
    
    when(textChannel.sendMessageFormat(anyString(), anyString())).thenReturn(action);
    
    run("b:dev.rank root");
    
    // Database should not have been updated
    UserEntity response = super.get(userTable, userEntity.getId());
    assertThat(response.getRanks()).isEqualTo(userEntity.getRanks());
    assertThat(response.getDiscriminator()).isEqualTo(userEntity.getDiscriminator());
    assertThat(response.getId()).isEqualTo(userEntity.getId());
    assertThat(response.getName()).isEqualTo(userEntity.getName());
  }
}
