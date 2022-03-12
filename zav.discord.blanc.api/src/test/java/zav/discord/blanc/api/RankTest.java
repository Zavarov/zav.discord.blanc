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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static zav.test.io.JsonUtils.read;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.UserTable;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Test class for checking user ranks.
 */
public class RankTest {
  UserTable userTable;
  UserEntity userEntity;
  User user;
  
  /**
   * Initializes all member variables.
   */
  @BeforeEach
  public void setUp() {
    Injector injector = Guice.createInjector();
    userTable = injector.getInstance(UserTable.class);
    userEntity = read("User.json", UserEntity.class);
    user = mock(User.class);
  }
  
  @AfterEach
  public void tearDown() throws Exception {
    Files.deleteIfExists(SqlQuery.ENTITY_DB_PATH);
    Files.deleteIfExists(SqlQuery.ENTITY_DB_PATH.getParent());
  }
  
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
  
  @Test
  public void testGetDefaultRanks() throws IOException {
    assertThat(Rank.getEffectiveRanks(userTable, user))
          .containsExactly(Rank.USER);
    
    Files.delete(SqlQuery.ENTITY_DB_PATH);
    
    assertThat(Rank.getEffectiveRanks(userTable, user))
          .containsExactly(Rank.USER);
  }
  
  @Test
  public void testGetEffectivePersistedRanks() throws SQLException {
    when(user.getIdLong()).thenReturn(userEntity.getId());
    
    userTable.put(userEntity);
    
    assertThat(Rank.getEffectiveRanks(userTable, user))
          .containsExactlyInAnyOrder(Rank.USER, Rank.DEVELOPER);
  }
}
