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

package zav.discord.blanc.command.internal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

import java.util.Collections;
import java.util.Set;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.InsufficientRankException;
import zav.discord.blanc.db.UserTable;

/**
 * Checks if the correct exception is thrown when called by a user with insufficient permissions.
 */
@ExtendWith(MockitoExtension.class)
public class RankValidatorTest {
  @Mock UserTable db;
  @Mock User author;
  
  RankValidator validator;
  MockedStatic<Rank> mocked;
  Set<Rank> ranks;
  
  
  /**
   * Initializes the permission validator. By default, every user is root.
   */
  @BeforeEach
  public void setUp() {
    validator = new RankValidator(db, author);
    
    ranks = Set.of(Rank.ROOT);
    
    mocked = mockStatic(Rank.class);
  }
  
  @AfterEach
  public void tearDown() {
    mocked.close();
  }
  
  /**
   * Use Case: Users with the required rank should pass the validation check.
   *
   * @throws Exception Thrown by the validation method.
   */
  @Test
  public void testValidate() throws Exception {
    mocked.when(() -> Rank.getEffectiveRanks(db, author)).thenReturn(Set.of(Rank.ROOT));
    
    validator.validate(ranks);
  }
  
  /**
   * Use Case: Users with insufficient rank should trigger an exception.
   */
  @Test
  public void testValidateWithInsufficientRanks() {
    mocked.when(() -> Rank.getEffectiveRanks(db, author)).thenReturn(Collections.emptySet());
    
    assertThrows(InsufficientRankException.class, () -> validator.validate(ranks));
  }
}
