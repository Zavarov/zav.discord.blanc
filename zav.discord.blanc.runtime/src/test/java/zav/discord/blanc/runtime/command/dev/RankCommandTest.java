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
import static org.mockito.Mockito.when;

import java.util.List;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.runtime.command.AbstractDevCommandTest;

/**
 * Check whether ranks can be granted and removed.
 */
public class RankCommandTest extends AbstractDevCommandTest {
  private @Mock OptionMapping arg;
  
  @Test
  public void testGrantRank() throws Exception {
    update(userTable, userEntity, e -> e.setRanks(List.of(Rank.ROOT.name())));
    
    when(user.getIdLong()).thenReturn(userEntity.getId());
    when(user.getName()).thenReturn(userEntity.getName());
    when(user.getDiscriminator()).thenReturn(userEntity.getDiscriminator());
    when(event.replyFormat(any(), any(), any())).thenReturn(reply);
    when(event.getOption("rank")).thenReturn(arg);
    when(arg.getAsString()).thenReturn(Rank.DEVELOPER.name());
    
    run(RankCommand.class);
    
    // Has the database been updated?
    UserEntity response = super.get(userTable, userEntity.getId());
    assertThat(response.getRanks()).containsExactly(Rank.ROOT.name(), Rank.DEVELOPER.name());
    assertThat(response.getDiscriminator()).isEqualTo(userEntity.getDiscriminator());
    assertThat(response.getId()).isEqualTo(userEntity.getId());
    assertThat(response.getName()).isEqualTo(userEntity.getName());
  }
  
  @Test
  public void testRemoveRank() throws Exception {
    update(userTable, userEntity, e -> e.setRanks(List.of(Rank.ROOT.name(), Rank.DEVELOPER.name())));
  
    when(user.getIdLong()).thenReturn(userEntity.getId());
    when(user.getName()).thenReturn(userEntity.getName());
    when(user.getDiscriminator()).thenReturn(userEntity.getDiscriminator());
    when(event.replyFormat(any(), any(), any())).thenReturn(reply);
    when(event.getOption("rank")).thenReturn(arg);
    when(arg.getAsString()).thenReturn(Rank.DEVELOPER.name());
  
    run(RankCommand.class);
  
    // Has the database been updated?
    UserEntity response = get(userTable, userEntity.getId());
    assertThat(response.getRanks()).containsExactly(Rank.ROOT.name());
    assertThat(response.getDiscriminator()).isEqualTo(userEntity.getDiscriminator());
    assertThat(response.getId()).isEqualTo(userEntity.getId());
    assertThat(response.getName()).isEqualTo(userEntity.getName());
  }
  
  @Test
  public void testInsufficientRank() throws Exception {
    update(userTable, userEntity, e -> e.setRanks(List.of(Rank.DEVELOPER.name())));
  
    when(user.getIdLong()).thenReturn(userEntity.getId());
    when(event.replyFormat(any(), any())).thenReturn(reply);
    when(event.getOption("rank")).thenReturn(arg);
    when(arg.getAsString()).thenReturn(Rank.ROOT.name());
    
    run(RankCommand.class);
    
    // Database should not have been updated
    UserEntity response = get(userTable, userEntity.getId());
    assertThat(response.getRanks()).containsExactly(Rank.DEVELOPER.name());
    assertThat(response.getDiscriminator()).isEqualTo(userEntity.getDiscriminator());
    assertThat(response.getId()).isEqualTo(userEntity.getId());
    assertThat(response.getName()).isEqualTo(userEntity.getName());
  }
}
