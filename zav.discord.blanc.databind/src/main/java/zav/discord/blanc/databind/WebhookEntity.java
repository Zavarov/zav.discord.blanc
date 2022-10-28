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

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Webhook;

@Getter
@Setter
@Generated
@NoArgsConstructor
@Entity
@Table(name = "Webhook")
public class WebhookEntity {
  /**
   * Unique 64bit long id of the webhook.
   */
  @Id
  private long id;
  
  /**
   * Human-readable name of the webhook.
   */
  private String name;
  
  /**
   * Boolean value specifying whether the webhook was created by this application.
   */
  private boolean owner;
  
  /**
   * The textchannel entity this object is contained by.
   */
  @ManyToOne
  private TextChannelEntity channel;
  
  /**
   * The guild entity this object is contained by.
   */
  @ManyToOne
  private GuildEntity guild;
  
  /**
   * Names of subreddits associated with this webhook.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @Column(length = 1000)
  private List<String> subreddits = new ArrayList<>();
  
  public static WebhookEntity getOrCreate(EntityManager entityManager, Webhook webhook) {
    WebhookEntity entity = entityManager.find(WebhookEntity.class, webhook.getIdLong());
    
    if (entity == null) {
      entity = new WebhookEntity();
      entity.setId(webhook.getIdLong());
    }
    
    // Webhook name may have changed since the last time the entity was persisted
    entity.setName(webhook.getName());
    
    return entity;
  }
  
  public boolean isEmpty() {
    return getSubreddits().isEmpty();
  }
}
