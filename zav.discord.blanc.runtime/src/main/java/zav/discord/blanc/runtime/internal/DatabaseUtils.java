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

package zav.discord.blanc.runtime.internal;

import com.google.common.collect.Lists;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.db.GuildTable;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.db.UserTable;
import zav.discord.blanc.db.WebhookTable;

/**
 * Utility class for serializing JDA entities.
 */
public final class DatabaseUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUtils.class);
  
  /**
   * Deserializes the given guild by looking up a matching entry in the database. If no entry is
   * found, a new object is constructed (but not added to the database).
   *
   * @param db The guild database.
   * @param guild A JDA guild.
   * @return An entity object corresponding to the guild.
   */
  public static GuildEntity getOrCreate(GuildTable db, Guild guild) {
    try {
      Optional<GuildEntity> response = db.get(guild);
      
      if (response.isEmpty()) {
        return new GuildEntity()
              .withId(guild.getIdLong())
              .withName(guild.getName());
      } else {
        // Guild name may have changed since the db entry was last updated
        return response.get().withName(guild.getName());
      }
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Deserializes the given text channel by looking up a matching entry in the database. If no
   * entry is found, a new object is constructed (but not added to the database).
   *
   * @param db The text channel database.
   * @param textChannel A JDA text channel.
   * @return An entity object corresponding to the text channel.
   * @deprecated Deprecated in favor of {@link #getOrCreate(WebhookTable, Webhook)}.
   */
  @Deprecated
  public static TextChannelEntity getOrCreate(TextChannelTable db, TextChannel textChannel) {
    try {
      Optional<TextChannelEntity> response = db.get(textChannel);
      
      if (response.isEmpty()) {
        return new TextChannelEntity()
              .withId(textChannel.getIdLong())
              .withGuildId(textChannel.getGuild().getIdLong())
              .withName(textChannel.getName());
      } else {
        // Channel name may have changed since the db entry was last updated
        return response.get().withName(textChannel.getName());
      }
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Deserializes the given user by looking up a matching entry in the database. If no entry is
   * found, a new object is constructed (but not added to the database).
   *
   * @param db The user database.
   * @param user A JDA user.
   * @return An entity object corresponding to the user.
   */
  public static UserEntity getOrCreate(UserTable db, User user) {
    try {
      Optional<UserEntity> response = db.get(user);
      
      if (response.isEmpty()) {
        return new UserEntity()
              .withId(user.getIdLong())
              .withName(user.getName())
              .withDiscriminator(user.getDiscriminator())
              .withRanks(Lists.newArrayList(Rank.USER.name()));
        
      } else {
        // Username may have changed since the db entry was last updated
        return response.get().withName(user.getName()).withDiscriminator(user.getDiscriminator());
      }
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Deserializes the given web hook by looking up a matching entry in the database. If no entry is
   * found, a new object is constructed (but not added to the database).
   *
   * @param db The webhook database.
   * @param webhook A JDA web hook.
   * @return An entity object corresponding to the web hook.
   */
  public static WebhookEntity getOrCreate(WebhookTable db, Webhook webhook) {
    try {
      Optional<WebhookEntity> response = db.get(webhook);
      
      if (response.isEmpty()) {
        @Nullable User owner = webhook.getOwnerAsUser();
        SelfUser selfUser = webhook.getJDA().getSelfUser();
        boolean isOwner = webhook.getOwnerAsUser() != null && Objects.equals(selfUser, owner);
  
        return new WebhookEntity()
              .withId(webhook.getIdLong())
              .withGuildId(webhook.getGuild().getIdLong())
              .withChannelId(webhook.getChannel().getIdLong())
              .withName(webhook.getName())
              .withOwner(isOwner);
      } else {
        // Webhook name may have changed since the db entry was last updated
        return response.get().withName(webhook.getName());
      }
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
}
