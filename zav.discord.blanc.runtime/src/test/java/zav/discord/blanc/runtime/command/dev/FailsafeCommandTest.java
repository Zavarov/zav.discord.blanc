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

import java.util.List;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.runtime.command.AbstractDevCommandTest;

/**
 * Check whether developer can request and relinquish super-user privileges.
 */
public class FailsafeCommandTest extends AbstractDevCommandTest {
  
  @Test
  public void testBecomeRoot() throws Exception {
    update(userTable, userEntity, e -> e.setRanks(List.of(Rank.DEVELOPER.name())));
  
    when(user.getIdLong()).thenReturn(userEntity.getId());
    when(user.getName()).thenReturn(userEntity.getName());
    when(user.getDiscriminator()).thenReturn(userEntity.getDiscriminator());
    when(user.getAsMention()).thenReturn(userEntity.getName());
    when(event.replyFormat(anyString(), anyString())).thenReturn(reply);
    
    run(FailsafeCommand.class);
  
    // Has the user become a root?
    UserEntity response = get(userTable, userEntity.getId());
    
    assertThat(response.getId()).isEqualTo(userEntity.getId());
    assertThat(response.getName()).isEqualTo(userEntity.getName());
    assertThat(response.getDiscriminator()).isEqualTo(userEntity.getDiscriminator());
    assertThat(response.getRanks()).contains(Rank.ROOT.name());
  }
  
  @Test
  public void testBecomeDeveloper() throws Exception {
    update(userTable, userEntity, e -> e.setRanks(List.of(Rank.ROOT.name())));
  
    when(user.getIdLong()).thenReturn(userEntity.getId());
    when(user.getName()).thenReturn(userEntity.getName());
    when(user.getDiscriminator()).thenReturn(userEntity.getDiscriminator());
    when(user.getAsMention()).thenReturn(userEntity.getName());
    when(event.replyFormat(anyString(), anyString())).thenReturn(reply);
  
    run(FailsafeCommand.class);
  
    // Has the user become a developer?
    UserEntity response = get(userTable, userEntity.getId());
  
    assertThat(response.getId()).isEqualTo(userEntity.getId());
    assertThat(response.getName()).isEqualTo(userEntity.getName());
    assertThat(response.getDiscriminator()).isEqualTo(userEntity.getDiscriminator());
    assertThat(response.getRanks()).contains(Rank.DEVELOPER.name());
  
  }
}
