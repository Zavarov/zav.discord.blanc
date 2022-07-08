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

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

@Getter
@Setter
@Generated
@NoArgsConstructor
@Entity
@Table(name = "User")
public class UserEntity {
  /**
   * Unique 64bit long id of the user.
   */
  @Id
  private long id;
  
  /**
   * Human-readable name of the user.
   */
  private String name;
  
  /**
   * Discriminator.
   */
  private String discriminator;
  
  /**
   * Program-specific user rank.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  private List<Rank> ranks = new ArrayList<>();
  
  public static UserEntity getOrCreate(EntityManager entityManager, User user) {
    UserEntity  entity = entityManager.find(UserEntity.class, user.getIdLong());
    
    if (entity == null) {
      entity = new UserEntity();
      entity.setId(user.getIdLong());
      entity.setRanks(List.of(Rank.USER));
    }
    
    // User name and discriminator may have changed since the last time the entity was persisted
    entity.setName(user.getName());
    entity.setDiscriminator(user.getDiscriminator());
    
    return entity;
  }
}
