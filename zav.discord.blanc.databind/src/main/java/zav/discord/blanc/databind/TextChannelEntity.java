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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;

@Getter
@Setter
@Generated
@NoArgsConstructor
@Entity
@Table(name = "TextChannel")
public class TextChannelEntity {
  /**
   * Unique 64bit long id of the guild.
   */
  @Id
  private long id;
  
  /**
   * Human-readable name of the channel.
   */
  private String name;
  
  /**
   * The guild entity this object is contained by.
   */
  @ManyToOne
  private GuildEntity guild;

  /**
   * Names of subreddits associated with this channel.
   */
  @Column(length = 1000)
  @ElementCollection(fetch = FetchType.EAGER)
  private List<String> subreddits = new ArrayList<>();
  
  /**
   * A list of all webhooks associated with this textchannel.
   */
  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<WebhookEntity> webhooks = new ArrayList<>();
  
  public void add(WebhookEntity entity) {
    remove(entity);
    
    getWebhooks().add(entity);
    entity.setChannel(this);
  }
  
  public void remove(WebhookEntity entity) {
    getWebhooks().removeIf(webhook -> webhook.getId() == entity.getId());
    entity.setChannel(null);
  }
  
  public static TextChannelEntity getOrCreate(EntityManager entityManager, TextChannel channel) {
    TextChannelEntity entity = entityManager.find(TextChannelEntity.class, channel.getIdLong());
    
    if (entity == null) {
      entity = new TextChannelEntity();
      entity.setId(channel.getIdLong());
    }
    
    // Text-channel name may have changed since the last time the entity was persisted
    entity.setName(channel.getName());
    
    return entity;
  }
  
  public boolean isEmpty() {
    return getSubreddits().isEmpty() && getWebhooks().isEmpty();
  }
}
