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

import static net.dv8tion.jda.api.entities.MessageEmbed.DESCRIPTION_MAX_LENGTH;
import static net.dv8tion.jda.api.entities.MessageEmbed.TITLE_MAX_LENGTH;
import static net.dv8tion.jda.api.entities.MessageEmbed.URL_MAX_LENGTH;

import java.awt.Color;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.LookupTranslator;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.jrc.databind.LinkEntity;

/**
 * Utility class for creating Discord messages displaying the relevant information about an entity
 * in a humanly readable format.
 */
@NonNullByDefault
public final class MessageUtils {
  
  private MessageUtils() {}
  
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtils.class);
  private static final CharSequenceTranslator UNESCAPE_REDDIT;
  
  static {
    Map<CharSequence, CharSequence> specialCharacters = new HashMap<>();
    specialCharacters.put("&#x200B;", StringUtils.EMPTY); // Zero-Width Character
    specialCharacters.put(":bugcatcher:", ":bug:");       // Used by r/DiscordApp flairs
    specialCharacters.put(":botdev:", "</>");

    LookupTranslator unescapeSpecialCharacters = new LookupTranslator(specialCharacters);

    UNESCAPE_REDDIT = new AggregateTranslator(
        StringEscapeUtils.UNESCAPE_HTML4,
        unescapeSpecialCharacters
      );
  }
  
  /**
   * Creates an embedded message displaying the relevant information of the Reddit link.
   *
   * @param link A Reddit link.
   * @return A JDA message embed displaying the relevant link information.
   */
  public static Message forLink(LinkEntity link) {
    return new MessageBuilder()
          .setContent(getPlainMessage(link))
          .setEmbeds(getRichMessage(link))
          .build();
  }
  
  private static String getPlainMessage(LinkEntity link) {
    return String.format(
          "New submission from u/%s in `r/%s`:%n%n<%s>",
          link.getAuthor(),
          link.getSubreddit(),
          getShortLink(link)
    );
  }
  
  private static MessageEmbed getRichMessage(LinkEntity link) {
    EmbedBuilder builder = new EmbedBuilder();
  
    builder.setTitle(getQualifiedTitle(link), getPermalink(link));
    builder.setAuthor("r/" + link.getSubreddit(), getUrl(link));
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
      builder.setDescription(getDescription(link));
    
      getThumbnail(link).ifPresent(thumbnail -> {
        if (EmbedBuilder.URL_PATTERN.matcher(thumbnail).matches()) {
          builder.setThumbnail(thumbnail);
        } else {
          LOGGER.debug("Thumbnail '{}' is not a valid URL.", thumbnail);
        }
      });
    }
    
    return builder.build();
  }
  
  private static String getQualifiedTitle(LinkEntity link) {
    StringBuilder builder = new StringBuilder();
    
    if (link.getLinkFlairText() != null) {
      if (!link.getLinkFlairText().startsWith("[")) {
        builder.append("[");
      }
      builder.append(UNESCAPE_REDDIT.translate(link.getLinkFlairText()));
      if (!link.getLinkFlairText().endsWith("]")) {
        builder.append("] ");
      }
    }
    
    if (link.getOver18()) {
      builder.append("[NSFW] ");
    }
  
    if (link.getOver18()) {
      builder.append("[Spoiler] ");
    }
    
    builder.append(getTitle(link));
    
    return StringUtils.truncate(builder.toString(), TITLE_MAX_LENGTH);
  }
  
  private static String getTitle(LinkEntity link) {
    String title = link.getTitle();
    return UNESCAPE_REDDIT.translate(title);
  }
  
  private static @Nullable String getPermalink(LinkEntity link) {
    @Nullable String permalink = "https://www.reddit.com" + link.getPermalink();
    return permalink.length() < URL_MAX_LENGTH ? permalink : null;
  }
  
  private static @Nullable String getUrl(LinkEntity link) {
    @Nullable String url = link.getUrl();
    return url != null && url.length() < URL_MAX_LENGTH ? url : null;
  }
  
  private static Optional<String> getThumbnail(LinkEntity link) {
    @Nullable String thumbnail = link.getThumbnail();
    
    if (thumbnail == null || thumbnail.length() >= URL_MAX_LENGTH) {
      return Optional.empty();
    }
    
    return Optional.of(thumbnail);
  }
  
  private static @Nullable String getDescription(LinkEntity link) {
    @Nullable
    String description = UNESCAPE_REDDIT.translate(link.getSelftext());
    return StringUtils.truncate(description, DESCRIPTION_MAX_LENGTH);
  }
  
  private static String getShortLink(LinkEntity link) {
    return "https://redd.it/" + link.getId();
  }
}
