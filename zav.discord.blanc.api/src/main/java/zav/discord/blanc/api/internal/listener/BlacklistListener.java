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

package zav.discord.blanc.api.internal.listener;

import static net.dv8tion.jda.api.Permission.MESSAGE_MANAGE;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.db.GuildDatabase;

/**
 * Listener for filtering banned expressions within a guild.<br>
 * The listener will intercept and check, whether it matches one of the blacklisted expressions in
 * a guild. Upon match, this message will then be deleted, assuming that the program has the
 * required permission to do so.
 */
public class BlacklistListener extends ListenerAdapter {
  private static final Cache<Long, Pattern> patternCache = CacheBuilder
        .newBuilder()
        .expireAfterAccess(Duration.ofHours(1))
        .build();
  
  private static final Logger LOGGER = LogManager.getLogger(AbstractCommandListener.class);
  
  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    // Exclude this bot from the blacklist
    if (isSelfUser(event.getAuthor())) {
      return;
    }
  
    // Only proceed when the bot has the required permissions for deleting messages
    if (!hasRequiredPermissions(event.getGuild(), event.getChannel())) {
      return;
    }
    
    @Nullable Pattern pattern = getPattern(event.getGuild().getIdLong());
    
    // No blacklist -> abort
    if (pattern == null) {
      return;
    }
    
    Message message = event.getMessage();
    
    if (MessageChecker.shouldDelete(message, pattern)) {
      LOGGER.info("Deleting message with id '{}'", message.getId());
      message.delete().complete();
    }
  }
  
  /**
   * Updates the pattern of banned expressions of the guild with the unique guild id.<br>
   * All messages matching this pattern are deleted automatically.
   *
   * @param guildId The unique id of a guild.
   * @param pattern The pattern all messages are matched against.
   */
  public static void setPattern(long guildId, @Nullable Pattern pattern) {
    if (pattern == null) {
      patternCache.invalidate(guildId);
    } else {
      patternCache.put(guildId, pattern);
    }
  }
  
  private @Nullable Pattern getPattern(long guildId) {
    @Nullable Pattern pattern = patternCache.getIfPresent(guildId);
    
    // Pattern has been cached
    if (pattern != null) {
      return pattern;
    }
    
    // Fetch pattern from database if it has already been garbage-collected.
    try {
      return GuildDatabase.get(guildId).getPattern();
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  private boolean isSelfUser(User author) {
    return author.equals(author.getJDA().getSelfUser());
  }

  private boolean hasRequiredPermissions(Guild guild, TextChannel channel) {
    return PermissionUtil.checkPermission(channel, guild.getSelfMember(), MESSAGE_MANAGE);
  }
  
  /**
   * Utility class to check whether a message has to be deleted.
   */
  private static class MessageChecker {
    /**
     * Checks whether the message contains a forbidden expression.<br>
     * Both the raw content of the message as well as all message embeds are validated.
     *
     * @param message A channel message.
     * @param pattern The pattern against which the content is validated.
     * @return {@code true}, if the message contains a forbidden expression.
     */
    public static boolean shouldDelete(Message message, Pattern pattern) {
      boolean shouldDelete = checkContent(message.getContentRaw(), pattern);
      
      if (shouldDelete) {
        return true;
      }
      
      shouldDelete = checkEmbeds(message.getEmbeds(), pattern);
      
      return shouldDelete;
    }
  
    /**
     * Checks whether the content of a message contains a forbidden expression.
     *
     * @param content The raw message content.
     * @param pattern The pattern against which the content is validated.
     * @return {@code true}, if the message contains a forbidden expression.
     */
    private static boolean checkContent(String content, Pattern pattern) {
      return pattern.matcher(content).find();
    }
  
    /**
     * Checks whether one of the message embed contains a forbidden expression.
     *
     * @param embeds All embeds of a message.
     * @param pattern The pattern against which the embeds is validated.
     * @return {@code true}, if at least one of the embeds contains a forbidden expression.
     */
    private static boolean checkEmbeds(List<MessageEmbed> embeds, Pattern pattern) {
      for (MessageEmbed embed : embeds) {
        if (checkEmbed(embed, pattern)) {
          return true;
        }
      }
      
      return false;
    }
  
    /**
     * Checks whether the message embed contains a forbidden expression.<br>
     * The title, url, description, footer and all fields are validated.
     *
     * @param embed One of the embeds of the message.
     * @param pattern The pattern against which the embed is validated.
     * @return {@code true}, if the embed contains a forbidden expression.
     */
    private static boolean checkEmbed(MessageEmbed embed, Pattern pattern) {
      if (embed.getTitle() != null && pattern.matcher(embed.getTitle()).find()) {
        return true;
      }
      
      if (embed.getUrl() != null && pattern.matcher(embed.getUrl()).find()) {
        return true;
      }
  
      if (embed.getDescription() != null && pattern.matcher(embed.getDescription()).find()) {
        return true;
      }
      
      if (embed.getFooter() != null && checkFooter(embed.getFooter(), pattern)) {
        return true;
      }
      
      for (MessageEmbed.Field field : embed.getFields()) {
        if (checkField(field, pattern)) {
          return true;
        }
      }
      
      return false;
    }
  
    /**
     * Checks whether the embed field contains an forbidden expression.<br>
     * Both the name and the value of the field is validated.
     *
     * @param field One of the fields of the message embed.
     * @param pattern The pattern against which the field is validated.
     * @return {@code true}, if the field contains a forbidden expression.
     */
    private static boolean checkField(MessageEmbed.Field field, Pattern pattern) {
      if (field.getName() != null && pattern.matcher(field.getName()).find()) {
        return true;
      }
  
      return field.getValue() != null && pattern.matcher(field.getValue()).find();
    }
  
    /**
     * Checks whether the embed footer contains an forbidden expression.
     *
     * @param footer The footer of the message embed..
     * @param pattern The pattern against which the field is validated.
     * @return {@code true}, if the field contains a forbidden expression.
     */
    private static boolean checkFooter(MessageEmbed.Footer footer, Pattern pattern) {
      return footer.getText() != null && pattern.matcher(footer.getText()).find();
    }
  }
}
