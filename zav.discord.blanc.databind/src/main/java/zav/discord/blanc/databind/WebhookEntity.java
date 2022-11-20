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
import zav.discord.blanc.databind.internal.PersistenceUtil;

/**
 * This PoJo corresponds to a Discord webhook and contains all channel-specific configurations.
 */
@Getter
@Setter
@Generated
@NoArgsConstructor
@Entity
@Table(name = "Webhook")
public class WebhookEntity implements PersistedEntity {
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

  /**
   * Checks whether this PoJo has any entries.
   *
   * @return {@code false} if this PoJo contains at least one entry, otherwise {@code true}.
   */
  public boolean isEmpty() {
    return getSubreddits().isEmpty();
  }

  /**
   * Removes the webhook from the database.
   *
   * @param webhook A Discord webhook.
   */
  public static void remove(Webhook webhook) {
    PersistenceUtil.remove(WebhookEntity.class, webhook);
  }

  /**
   * Returns the PoJo associated with the provided webhook. It first attempts to load the PoJo from
   * the database. If no such entry exists, a new entry is created.
   * 
   * @param webhook A Discord webhook.
   * @return The PoJo corresponding to the webhook.
   */
  public static WebhookEntity find(Webhook webhook) {
    return PersistenceUtil.find(webhook);
  }
}
