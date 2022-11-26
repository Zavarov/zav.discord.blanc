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
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import zav.discord.blanc.databind.internal.PersistenceUtil;

/**
 * This PoJo corresponds to a Discord guild and contains all guild-specific configurations.
 */
@Getter
@Setter
@Generated
@NoArgsConstructor
@Entity
@Table(name = "Guild")
public class GuildEntity implements PersistedEntity {
  /**
   * Unique 64bit long id of the guild.
   */
  @Id
  private long id;
  /**
   * Human-readable name of the guild.
   */
  private String name;
  
  /**
   * A list of all webhook entities associated with this guild.
   */
  @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<WebhookEntity> webhooks = new ArrayList<>();

  
  /**
   * A list of all textchannel entities associated with this guild.
   */
  @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TextChannelEntity> textChannels = new ArrayList<>();

  
  /**
   * A list of registered auto-responses.
   */
  @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AutoResponseEntity> autoResponses = new ArrayList<>();
  
  /**
   * Adds the given entity to this PoJo. If an entity with this id already exists, it is replaced
   * with the new object.
   *
   * @param entity The object to add to this PoJo.
   */
  public void add(TextChannelEntity entity) {
    remove(entity);
    
    getTextChannels().add(entity);
    entity.setGuild(this);
  }
  
  /**
   * Adds the given entity to this PoJo. If an entity with this id already exists, it is replaced
   * with the new object.
   *
   * @param entity The object to add to this PoJo.
   */
  public void add(WebhookEntity entity) {
    remove(entity);
    
    getWebhooks().add(entity);
    entity.setGuild(this);
  }
  
  /**
   * Adds the given entity to this PoJo. If an entity with this id already exists, it is replaced
   * with the new object.
   *
   * @param entity The object to add to this PoJo.
   */
  public void add(AutoResponseEntity entity) {
    remove(entity);
    
    getAutoResponses().add(entity);
    entity.setGuild(this);
  }
  
  /**
   * Removes the entity from this PoJo using its id. Does nothing of the entity is not contained in
   * this guild.
   *
   * @param entity The object to remove from this PoJo.
   */
  public void remove(TextChannelEntity entity) {
    getTextChannels().removeIf(channel -> channel.getId() == entity.getId());
    entity.setGuild(null);
  }
  
  /**
   * Removes the entity from this PoJo using its id. Does nothing of the entity is not contained in
   * this guild.
   *
   * @param entity The object to remove from this PoJo.
   */
  public void remove(WebhookEntity entity) {
    getWebhooks().removeIf(webhook -> webhook.getId() == entity.getId());
    entity.setGuild(null);
  }
  
  /**
   * Removes the entity from this PoJo using its id. Does nothing of the entity is not contained in
   * this guild.
   *
   * @param entity The object to remove from this PoJo.
   */
  public void remove(AutoResponseEntity entity) {
    getAutoResponses().removeIf(response -> response.getId() == entity.getId());
    entity.setGuild(null);
  }

  /**
   * Removes the guild from the database.
   *
   * @param guild A Discord guild.
   */
  public static void remove(Guild guild) {
    PersistenceUtil.remove(GuildEntity.class, guild);
  }

  /**
   * Returns the PoJo associated with the provided guild. It first attempts to load the PoJo from
   * the database. If no such entry exists, a new entry is created.
   * 
   * @param guild A Discord guild.
   * @return The PoJo corresponding to the guild.
   */
  public static GuildEntity find(Guild guild) {
    return PersistenceUtil.find(guild);
  }
}
