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

package zav.discord.blanc.api.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.UserTable;

/**
 * Test class for checking user ranks.
 */
@ExtendWith(MockitoExtension.class)
public class RankTest {
  @Mock UserEntity userEntity;
  @Mock UserTable userTable;
  @Mock User user;
  
  /**
   * Parameter source for {@link #testGetEffectiveRanks(String, Set)}.
   *
   * @return The arguments provided for unit test.
   */
  @SuppressWarnings("unused") // used via @MethodSource
  public static Stream<Arguments> testGetEffectiveRanks() {
    return Stream.of(
          Arguments.of("reddit", EnumSet.of(Rank.USER, Rank.REDDIT)),
          Arguments.of("user", EnumSet.of(Rank.USER)),
          Arguments.of("developer", EnumSet.of(Rank.USER, Rank.DEVELOPER)),
          Arguments.of("root", EnumSet.allOf(Rank.class))
    );
  }
  
  @ParameterizedTest
  @MethodSource
  public void testGetEffectiveRanks(String name, Set<Rank> effectiveRanks) {
    assertEquals(Rank.getEffectiveRanks(List.of(name)), effectiveRanks);
  }
  
  @Test
  public void testGetDefaultRanks() throws SQLException {
    assertThat(Rank.getEffectiveRanks(userTable, user))
          .containsExactly(Rank.USER);
    
    when(userTable.get(any(User.class))).thenThrow(SQLException.class);

    // Also return the default during an database error
    assertThat(Rank.getEffectiveRanks(userTable, user))
          .containsExactly(Rank.USER);
  }
  
  @Test
  public void testGetEffectivePersistedRanks() throws SQLException {
    when(userTable.get(any(User.class))).thenReturn(Optional.of(userEntity));
    when(userEntity.getRanks()).thenReturn(List.of("DEVELOPER"));
    
    assertThat(Rank.getEffectiveRanks(userTable, user))
          .containsExactlyInAnyOrder(Rank.USER, Rank.DEVELOPER);
  }
}
