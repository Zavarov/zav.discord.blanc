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

package zav.discord.blanc.databind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for checking user ranks.
 */
@ExtendWith(MockitoExtension.class)
public class RankTest {
  UserEntity entity;
  @Mock EntityManagerFactory mockFactory;
  @Mock EntityManager mockManager;
  @Mock User user;
  
  @BeforeEach
  public void setUp() {
    entity = new UserEntity();
  }
  
  /**
   * Parameter source for {@link #testGetEffectiveRanks(Rank, Set)}.
   *
   * @return The arguments provided for unit test.
   */
  public static Stream<Arguments> testGetEffectiveRanks() {
    return Stream.of(
          Arguments.of(Rank.USER, EnumSet.of(Rank.USER)),
          Arguments.of(Rank.DEVELOPER, EnumSet.of(Rank.USER, Rank.DEVELOPER)),
          Arguments.of(Rank.ROOT, EnumSet.allOf(Rank.class))
    );
  }
  
  /**
   * Use Case: A user with special rank tries to execute a command.
   *
   * @param rank The rank granted to the user.
   * @param effectiveRanks The effect rank the user holds.
   */
  @ParameterizedTest
  @MethodSource
  public void testGetEffectiveRanks(Rank rank, Set<Rank> effectiveRanks) {
    entity.setRanks(List.of(rank));
    entity.merge();
    
    when(user.getIdLong()).thenReturn(entity.getId());
    
    assertEquals(Rank.getEffectiveRanks(user), effectiveRanks);
  }
  
  /**
   * Use Case : A normal user tries to execute a command.
   */
  //@Test
  public void testGetDefaultRanks() {
    when(user.getIdLong()).thenReturn(entity.getId());
    assertEquals(Rank.getEffectiveRanks(user), EnumSet.of(Rank.USER));
  }
}
