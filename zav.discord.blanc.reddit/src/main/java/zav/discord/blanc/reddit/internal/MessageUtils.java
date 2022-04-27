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

package zav.discord.blanc.reddit.internal;

import static net.dv8tion.jda.api.EmbedBuilder.URL_PATTERN;
import static net.dv8tion.jda.api.entities.MessageEmbed.DESCRIPTION_MAX_LENGTH;
import static net.dv8tion.jda.api.entities.MessageEmbed.TITLE_MAX_LENGTH;
import static net.dv8tion.jda.api.entities.MessageEmbed.URL_MAX_LENGTH;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.truncate;

import java.awt.Color;
import java.time.Instant;
import java.util.Objects;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.jrc.databind.LinkDto;

/**
 * Utility class for creating Discord messages displaying the relevant information about an entity
 * in a humanly readable format.
 */
public final class MessageUtils {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtils.class);
  
  /**
   * Creates an embedded message displaying the relevant information of the Reddit link.
   *
   * @param link A Reddit link.
   * @return A JDA message embed displaying the relevant link information.
   */
  public static Message forLink(LinkDto link) {
    String qualifiedTitle =
          (link.getLinkFlairText() != null ? "[" + link.getLinkFlairText() + "]" : EMPTY)
          + (link.getOver18() ? "[NSFW] " : EMPTY)
          + (link.getSpoiler() ? "[Spoiler] " : EMPTY)
          + link.getTitle();
    qualifiedTitle = truncate(qualifiedTitle, TITLE_MAX_LENGTH);
    
    @Nullable String permalink = "https://www.reddit.com" + link.getPermalink();
    permalink = (permalink.length() < URL_MAX_LENGTH) ? permalink : null;
  
    @Nullable String url = link.getUrl();
    url = (url != null && url.length() < URL_MAX_LENGTH) ? url : null;
    
    @Nullable String thumbnail = link.getThumbnail();
    thumbnail = (thumbnail != null && thumbnail.length() < URL_MAX_LENGTH) ? thumbnail : null;
    
    @Nullable String description = link.getSelftext();
    description = truncate(description, DESCRIPTION_MAX_LENGTH);
  
    EmbedBuilder builder = new EmbedBuilder();
    
    builder.setTitle(qualifiedTitle, permalink);
    builder.setAuthor("source", url);
    
    builder.addField("Author", link.getAuthor(), false);
  
    if (link.getCreatedUtc() != null) {
      builder.setTimestamp(Instant.ofEpochSecond(link.getCreatedUtc().longValue()));
    }
  
    if (link.getOver18()) {
      builder.setColor(Color.RED);
    } else if (link.getSpoiler()) {
      builder.setColor(Color.BLACK);
    } else {
      builder.setColor(new Color(Objects.hashCode(link.getLinkFlairText())));
      builder.setDescription(description);
  
      if (thumbnail != null && URL_PATTERN.matcher(thumbnail).matches()) {
        builder.setThumbnail(thumbnail);
      } else if (thumbnail != null) {
        LOGGER.debug("Thumbnail '{}' is not a valid URL.", thumbnail);
      }
    }
  
    String shortlink = "https://redd.it/" + link.getId();
    String content = String.format(
          "New submission from u/%s in `r/%s`:\n\n<%s>",
          link.getAuthor(),
          link.getName(),
          shortlink
    );
    MessageEmbed embed = builder.build();
  
    MessageBuilder messageBuilder = new MessageBuilder();
    messageBuilder.setEmbeds(embed);
    messageBuilder.setContent(content);
  
    return messageBuilder.build();
  }
}
