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
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.Set;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.command.InsufficientRankException;
import zav.discord.blanc.databind.Rank;
import zav.discord.blanc.databind.UserEntity;

/**
 * Checks if the correct exception is thrown when called by a user with insufficient permissions.
 */
@ExtendWith(MockitoExtension.class)
public class RankValidatorTest {
  EntityManagerFactory factory;
  EntityManager entityManager;
  UserEntity userEntity;
  
  @Mock User author;
  
  RankValidator validator;
  Set<Rank> ranks;
  
  /**
   * Initializes the permission validator. By default, every user is root.
   */
  @BeforeEach
  public void setUp() {
    factory = Persistence.createEntityManagerFactory("discord-entities");
    entityManager = factory.createEntityManager();
    userEntity = new UserEntity();
    userEntity.setRanks(List.of(Rank.ROOT));
    validator = new RankValidator(factory, author);
    ranks = Set.of(Rank.ROOT);
  }
  
  @AfterEach
  public void tearDown() {
    entityManager.close();
    factory.close();
  }
  
  /**
   * Use Case: Users with the required rank should pass the validation check.
   *
   * @throws Exception Thrown by the validation method.
   */
  @Test
  public void testValidate() throws Exception {
    entityManager.getTransaction().begin();
    entityManager.merge(userEntity);
    entityManager.getTransaction().commit();
    
    when(author.getIdLong()).thenReturn(userEntity.getId());
    validator.validate(ranks);
  }
  
  /**
   * Use Case: Users with insufficient rank should trigger an exception.
   */
  @Test
  public void testValidateWithInsufficientRanks() {
    assertThrows(InsufficientRankException.class, () -> validator.validate(ranks));
  }
}
