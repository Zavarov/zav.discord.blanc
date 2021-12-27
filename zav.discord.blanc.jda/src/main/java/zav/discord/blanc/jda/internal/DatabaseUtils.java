/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.jda.internal;

import com.google.common.collect.Lists;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Objects;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.command.Rank;
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.databind.RoleValueObject;
import zav.discord.blanc.databind.TextChannelValueObject;
import zav.discord.blanc.databind.UserValueObject;
import zav.discord.blanc.databind.WebHookValueObject;
import zav.discord.blanc.db.GuildDatabase;
import zav.discord.blanc.db.RoleDatabase;
import zav.discord.blanc.db.TextChannelDatabase;
import zav.discord.blanc.db.UserDatabase;
import zav.discord.blanc.db.WebHookDatabase;

/**
 * Utility class for serializing JDA entities.
 */
public final class DatabaseUtils {
  private static final Logger LOGGER = LogManager.getLogger(DatabaseUtils.class);
  
  /**
   * Deserializes the given guild by looking up a matching entry in the database. If no entry is
   * found, a new object is constructed (but not added to the database).
   *
   * @param guild A JDA guild.
   * @return A JSON object corresponding to the guild.
   */
  public static GuildValueObject aboutGuild(Guild guild) {
    try {
      // Guild name may have changed since the db entry was last updated
      return GuildDatabase.get(guild.getIdLong())
            .withName(guild.getName());
    } catch (NoSuchElementException e) {
      return new GuildValueObject()
            .withId(guild.getIdLong())
            .withName(guild.getName());
      
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Deserializes the given role by looking up a matching entry in the database. If no entry is
   * found, a new object is constructed (but not added to the database).
   *
   * @param role A JDA role.
   * @return A JSON object corresponding to the role.
   */
  public static RoleValueObject aboutRole(Role role) {
    try {
      long guildId = role.getGuild().getIdLong();
      long roleId = role.getIdLong();
      
      // Role name may have changed since the last time the database was updated
      return RoleDatabase.get(guildId, roleId)
            .withName(role.getName());
    } catch (NoSuchElementException e) {
      return new RoleValueObject()
            .withId(role.getIdLong())
            .withName(role.getName());
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Deserializes the given self user by looking up a matching entry in the database. If no entry is
   * found, a new object is constructed (but not added to the database).
   *
   * @param selfUser A JDA self user.
   * @return A JSON object corresponding to the self user.
   */
  public static UserValueObject aboutSelfUser(SelfUser selfUser) {
    try {
      // Guild name may have changed since the db entry was last updated
      return UserDatabase.get(selfUser.getIdLong())
            .withName(selfUser.getName());
    } catch (NoSuchElementException e) {
      return new UserValueObject()
            .withId(selfUser.getIdLong())
            .withName(selfUser.getName())
            .withDiscriminator(Long.parseLong(selfUser.getDiscriminator()))
            .withRanks(Lists.newArrayList(Rank.USER.name()));
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Deserializes the given text channel by looking up a matching entry in the database. If no
   * entry is found, a new object is constructed (but not added to the database).
   *
   * @param textChannel A JDA text channel.
   * @return A JSON object corresponding to the text channel.
   */
  public static TextChannelValueObject aboutTextChannel(TextChannel textChannel) {
    try {
      long guildId = textChannel.getGuild().getIdLong();
      long channelId = textChannel.getIdLong();
      
      // Channel name may have changed since the db entry was last updated
      return TextChannelDatabase.get(guildId, channelId)
            .withName(textChannel.getName());
    } catch (NoSuchElementException e) {
      return new TextChannelValueObject()
            .withId(textChannel.getIdLong())
            .withName(textChannel.getName());
      
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Deserializes the given user by looking up a matching entry in the database. If no entry is
   * found, a new object is constructed (but not added to the database).
   *
   * @param user A JDA user.
   * @return A JSON object corresponding to the user.
   */
  public static UserValueObject aboutUser(User user) {
    try {
      
      // User name may have changed since the db entry was last updated
      return UserDatabase.get(user.getIdLong())
            .withName(user.getName());
    } catch (NoSuchElementException e) {
      return new UserValueObject()
            .withId(user.getIdLong())
            .withName(user.getName())
            .withDiscriminator(Long.parseLong(user.getDiscriminator()))
            .withRanks(Lists.newArrayList(Rank.USER.name()));
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Deserializes the given web hook by looking up a matching entry in the database. If no entry is
   * found, a new object is constructed (but not added to the database).
   *
   * @param webHook A JDA web hook.
   * @return A JSON object corresponding to the web hook.
   */
  public static WebHookValueObject aboutWebHook(Webhook webHook) {
    try {
      long guildId = webHook.getGuild().getIdLong();
      long channelId = webHook.getChannel().getIdLong();
      long webHookId = webHook.getIdLong();
      
      // WebHook name may have changed since the db entry was last updated
      return WebHookDatabase.get(guildId, channelId, webHookId)
            .withName(webHook.getName());
    } catch (NoSuchElementException e) {
      @Nullable User owner = webHook.getOwnerAsUser();
      SelfUser selfUser = webHook.getJDA().getSelfUser();
      boolean isOwner = webHook.getOwnerAsUser() != null && Objects.equals(selfUser, owner);
      
      return new WebHookValueObject()
            .withId(webHook.getIdLong())
            .withChannelId(webHook.getChannel().getIdLong())
            .withName(webHook.getName())
            .withOwner(isOwner);
      
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
}
