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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import zav.discord.blanc.databind.internal.PersistenceUtil;

/**
 * This PoJo corresponds to a Discord text channel and contains all channel-specific configurations.
 */
@Getter
@Setter
@Generated
@NoArgsConstructor
@Entity
@Table(name = "TextChannel")
public class TextChannelEntity implements PersistedEntity {
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
  
  /**
   * Adds the given entity to this PoJo. If an entity with this id already exists, it is replaced
   * with the new object.
   *
   * @param entity The object to add to this PoJo.
   */
  public void add(WebhookEntity entity) {
    remove(entity);
    
    getWebhooks().add(entity);
    entity.setChannel(this);
  }
  
  /**
   * Removes the entity from this PoJo using its id. Does nothing of the entity is not contained in
   * this guild.
   *
   * @param entity The object to remove from this PoJo.
   */
  public void remove(WebhookEntity entity) {
    getWebhooks().removeIf(webhook -> webhook.getId() == entity.getId());
    entity.setChannel(null);
  }
  
  /**
   * Checks whether this PoJo has any entries.
   *
   * @return {@code false} if this PoJo contains at least one entry, otherwise {@code true}.
   */
  public boolean isEmpty() {
    return getSubreddits().isEmpty() && getWebhooks().isEmpty();
  }

  /**
   * Removes the guild from the database.
   *
   * @param guild A Discord guild.
   */
  public static void remove(Guild guild) {
    PersistenceUtil.remove(guild);
  }

  /**
   * Removes the text channel from the database.
   *
   * @param channel A Discord channel.
   */
  public static void remove(TextChannel channel) {
    PersistenceUtil.remove(channel);
  }

  /**
   * Returns the PoJo associated with the provided channel. It first attempts to load the PoJo from
   * the database. If no such entry exists, a new entry is created.
   * 
   * @param channel A Discord text channel.
   * @return The PoJo corresponding to the channel.
   */
  public static TextChannelEntity find(TextChannel channel) {
    return PersistenceUtil.find(channel);
  }
}
