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

package zav.discord.blanc.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test class for checking user ranks.
 */
public class RankTest {
  
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
    assertThat(Rank.getEffectiveRanks(List.of(name))).isEqualTo(effectiveRanks);
  }
}
