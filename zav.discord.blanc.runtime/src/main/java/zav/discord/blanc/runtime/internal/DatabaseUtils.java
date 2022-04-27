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
import java.util.List;
import java.util.Objects;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.databind.WebHookEntity;
import zav.discord.blanc.db.GuildTable;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.db.UserTable;
import zav.discord.blanc.db.WebHookTable;

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
      long guildId = guild.getIdLong();
      
      List<GuildEntity> responses = db.get(guildId);
  
      Validate.validState(responses.size() <= 1);
      
      if (responses.isEmpty()) {
        return new GuildEntity()
              .withId(guild.getIdLong())
              .withName(guild.getName());
      } else {
        // Guild name may have changed since the db entry was last updated
        return responses.get(0)
              .withName(guild.getName());
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
   * @deprecated Deprecated in favor of {@link #getOrCreate(WebHookTable, Webhook)}.
   */
  @Deprecated
  public static TextChannelEntity getOrCreate(TextChannelTable db, TextChannel textChannel) {
    try {
      long guildId = textChannel.getGuild().getIdLong();
      long channelId = textChannel.getIdLong();
      
      List<TextChannelEntity> responses = db.get(guildId, channelId);
  
      Validate.validState(responses.size() <= 1);
      
      if (responses.isEmpty()) {
        return new TextChannelEntity()
              .withId(textChannel.getIdLong())
              .withGuildId(guildId)
              .withName(textChannel.getName());
      } else {
        // Channel name may have changed since the db entry was last updated
        return responses.get(0)
              .withName(textChannel.getName());
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
      long userId = user.getIdLong();
  
      List<UserEntity> responses = db.get(userId);
  
      Validate.validState(responses.size() <= 1);
      
      if (responses.isEmpty()) {
        return new UserEntity()
              .withId(userId)
              .withName(user.getName())
              .withDiscriminator(user.getDiscriminator())
              .withRanks(Lists.newArrayList(Rank.USER.name()));
        
      } else {
        // Username may have changed since the db entry was last updated
        return responses.get(0)
              .withName(user.getName())
              .withDiscriminator(user.getDiscriminator());
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
  public static WebHookEntity getOrCreate(WebHookTable db, Webhook webhook) {
    try {
      long guildId = webhook.getGuild().getIdLong();
      long channelId = webhook.getChannel().getIdLong();
      long webhookId = webhook.getIdLong();
      
      List<WebHookEntity> responses = db.get(guildId, channelId, webhookId);
  
      Validate.validState(responses.size() <= 1);
      
      if (responses.isEmpty()) {
        @Nullable User owner = webhook.getOwnerAsUser();
        SelfUser selfUser = webhook.getJDA().getSelfUser();
        boolean isOwner = webhook.getOwnerAsUser() != null && Objects.equals(selfUser, owner);
  
        return new WebHookEntity()
              .withId(webhookId)
              .withGuildId(guildId)
              .withChannelId(channelId)
              .withName(webhook.getName())
              .withOwner(isOwner);
      } else {
        // Webhook name may have changed since the db entry was last updated
        return responses.get(0)
              .withName(webhook.getName());
      }
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
}
