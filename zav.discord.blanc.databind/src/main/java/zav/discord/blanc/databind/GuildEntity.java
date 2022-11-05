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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import zav.discord.blanc.databind.internal.PersistenceUtil;

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
   * Collection of banned words.
   */
  @Column(length = 1000)
  @ElementCollection(fetch = FetchType.EAGER)
  private List<String> blacklist = new ArrayList<>();
  
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
   * Each expression is concatenated using an {@code or}, meaning the pattern will match any String
   * that matches at least one banned expression.<br>
   * This method acts as a utility function to simplify the transformation of multiple Strings into
   * a single pattern.
   *
   * @return The pattern corresponding to all blacklisted expressions.
   */
  public Optional<Pattern> getPattern() {
    return getBlacklist().stream()
          .reduce((u, v) -> u + "|" + v)
          .map(Pattern::compile);
  }
  
  public void add(TextChannelEntity entity) {
    remove(entity);
    
    getTextChannels().add(entity);
    entity.setGuild(this);
  }
  
  public void add(WebhookEntity entity) {
    remove(entity);
    
    getWebhooks().add(entity);
    entity.setGuild(this);
  }
  
  public void add(AutoResponseEntity entity) {
    remove(entity);
    
    getAutoResponses().add(entity);
    entity.setGuild(this);
  }
  
  public void remove(TextChannelEntity entity) {
    getTextChannels().removeIf(channel -> channel.getId() == entity.getId());
    entity.setGuild(null);
  }
  
  public void remove(WebhookEntity entity) {
    getWebhooks().removeIf(webhook -> webhook.getId() == entity.getId());
    entity.setGuild(null);
  }
  
  public void remove(AutoResponseEntity entity) {
    getAutoResponses().removeIf(response -> response.getId() == entity.getId());
    entity.setGuild(null);
  }

  public static void remove(Guild guild) {
    PersistenceUtil.remove(guild);
  }

  public static GuildEntity find(Guild guild) {
    return PersistenceUtil.find(guild);
  }
}
