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

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The PoJo contains all automatic responses which are registered within a guild.
 */
@Getter
@Setter
@Generated
@NoArgsConstructor
@Entity
@Table(name = "AutoResponse")
public class AutoResponseEntity implements PersistedEntity {
  
  /**
   * The guild entity this object is contained by.
   */
  @ManyToOne
  private GuildEntity guild;
  
  /**
   * The expression all messages are matched against.
   */
  private String pattern;
  
  /**
   * The message that is returned on a match.
   */
  private String answer;
  
  /**
   * The internal entity id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
  /**
   * Creates a new instance of this PoJo. Given that the id of this object is auto-generated, it is
   * impossible to lookup an already existing entry. Instead, a new instance has to be created every
   * time.
   *
   * @param regex The pattern the response is matched against.
   * @param answer The response on a match.
   * @return A new instance of this class.
   */
  public static AutoResponseEntity create(String regex, String answer) {
    AutoResponseEntity entity = new AutoResponseEntity();
    
    entity = new AutoResponseEntity();
    entity.setPattern(regex);
    entity.setAnswer(answer);
    
    return entity;
  }
}
