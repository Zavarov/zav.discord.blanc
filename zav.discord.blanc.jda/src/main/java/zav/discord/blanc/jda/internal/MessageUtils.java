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

import static net.dv8tion.jda.api.entities.MessageEmbed.DESCRIPTION_MAX_LENGTH;
import static net.dv8tion.jda.api.entities.MessageEmbed.TITLE_MAX_LENGTH;
import static net.dv8tion.jda.api.entities.MessageEmbed.URL_MAX_LENGTH;

import java.awt.Color;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.Nullable;
import zav.jrc.databind.LinkValueObject;

import static net.dv8tion.jda.api.EmbedBuilder.URL_PATTERN;

/**
 * Utility class for creating Discord messages displaying the relevant information about an entity
 * in a humanly readable format.
 */
public final class MessageUtils {
  /**
   * A moderator is every user that has as least one of the listed permissions.
   */
  protected static final Set<Permission> MODERATOR_PERMISSIONS = Set.of(
        Permission.BAN_MEMBERS,
        Permission.KICK_MEMBERS,
        Permission.MANAGE_CHANNEL,
        Permission.MANAGE_PERMISSIONS,
        Permission.MANAGE_ROLES,
        Permission.MANAGE_SERVER
  );
  /**
   * An administrator is every user that has all of the listed permissions.
   */
  protected static final Set<Permission> ADMINISTRATOR_PERMISSIONS = Set.of(
        Permission.ADMINISTRATOR
  );
  /**
   * The date pretty printer.
   */
  protected static final DateTimeFormatter DATE = DateTimeFormatter.RFC_1123_DATE_TIME;
  
  private static final Logger LOGGER = LogManager.getLogger(MessageUtils.class);
  
  /**
   * Creates an embedded message displaying the relevant information of the JDA user.
   *
   * @param jdaUser A JDA user.
   * @return A JDA message embed displaying the relevant user information.
   */
  public static MessageEmbed forUser(User jdaUser) {
    EmbedBuilder builder = new EmbedBuilder();
  
    builder.setTitle(jdaUser.getAsTag());
    builder.setThumbnail(jdaUser.getEffectiveAvatarUrl());
  
    // ID
    builder.addField("ID", jdaUser.getId(), false);
  
    // Creation time
    Period period = Period.between(jdaUser.getTimeCreated().toLocalDate(), LocalDate.now());
    String message = String.format(
          "%s\n(%d %s, %d %s and %d %s ago)",
          DATE.format(jdaUser.getTimeCreated()),
          period.getYears(), "year(s)",
          period.getMonths(), "month(s)",
          period.getDays(), "day(s)"
    );
  
    builder.addField("Created", message, false);
  
    return builder.build();
  }
  
  /**
   * Creates an embedded message displaying the relevant information of the JDA member.
   *
   * @param jdaMember A JDA member.
   * @return A JDA message embed displaying the relevant member information.
   */
  public static MessageEmbed forMember(Member jdaMember) {
    EmbedBuilder builder = new EmbedBuilder();
  
    builder.setTitle(jdaMember.getUser().getAsTag());
    builder.setThumbnail(jdaMember.getUser().getEffectiveAvatarUrl());
  
    // Nickname
    if (jdaMember.getNickname() != null) {
      builder.addField("Nickname", jdaMember.getNickname(), true);
    }
  
    // ID
    builder.addField("ID", jdaMember.getId(), false);
  
    // Color
    @Nullable Color color = jdaMember.getColor();
  
    if (color != null) {
      builder.addField(
            "Color",
            String.format("0x%02X%02X%02X",
                  color.getRed(),
                  color.getGreen(),
                  color.getBlue()
            ),
            true
      );
    }
  
    // Role count
    builder.addField("#Roles", Integer.toString(jdaMember.getRoles().size()), true);
  
    // Creation time
    Period period = Period.between(jdaMember.getTimeCreated().toLocalDate(), LocalDate.now());
    String message = String.format(
          "%s\n(%d %s, %d %s and %d %s ago)",
          DATE.format(jdaMember.getTimeCreated()),
          period.getYears(), "year(s)",
          period.getMonths(), "month(s)",
          period.getDays(), "day(s)"
    );
  
    builder.addField("Created", message, false);
  
    // Join time
    period = Period.between(jdaMember.getTimeJoined().toLocalDate(), LocalDate.now());
    message = String.format(
          "%s\n(%d %s, %d %s and %d %s ago)",
          DATE.format(jdaMember.getTimeCreated()),
          period.getYears(), "year(s)",
          period.getMonths(), "month(s)",
          period.getDays(), "day(s)"
    );
  
    builder.addField("Joined", message, true);
  
    for (Activity activity : jdaMember.getActivities()) {
      String type;
      //Transform the game type into an user friendly string
      if (activity.getType() == Activity.ActivityType.DEFAULT) {
        type = "Playing";
      } else if (activity.getType() == Activity.ActivityType.CUSTOM_STATUS) {
        type = "Custom Status";
      } else {
        type = activity.getType().name().toLowerCase(Locale.ENGLISH);
        type = StringUtils.capitalize(type);
      }
      builder.addField(type, activity.getName(), false);
    }
  
    return builder.build();
  }
  
  /**
   * Creates an embedded message displaying the relevant information of the JDA guild.
   *
   * @param jdaGuild A JDA guild.
   * @return A JDA message embed displaying the relevant guild information.
   */
  public static MessageEmbed forGuild(Guild jdaGuild) {
    EmbedBuilder builder = new EmbedBuilder();
  
    builder.setTitle(jdaGuild.getName());
  
    // Icon may be null if the guild uses the default icon
    if (jdaGuild.getIconUrl() != null) {
      builder.setThumbnail(jdaGuild.getIconUrl());
    }
  
    //Owner may be null if the account was suspended
    if (jdaGuild.getOwner() != null) {
      builder.addField("Owner", jdaGuild.getOwner().getAsMention(), true);
    }
  
    // Add channels
    builder.addField("#Text Channel(s)", Integer.toString(jdaGuild.getTextChannels().size()), true);
    builder.addField("#Voice Channel(s)", Integer.toString(jdaGuild.getVoiceChannels().size()), true);
  
    // Add admins
    List<Member> administrators = jdaGuild.getMembers()
          .stream()
          .filter(m -> !m.getUser().isBot())
          .filter(m -> m.getPermissions().containsAll(ADMINISTRATOR_PERMISSIONS))
          .sorted(Comparator.comparingLong(u -> u.getUser().getIdLong()))
          .collect(Collectors.toList());
  
    administrators.stream()
          .map(Member::getAsMention)
          .reduce((u, v) -> u + "\n" + v)
          .ifPresent(content -> builder.addField("Admin(s)", content, false));
  
    // Add mods
    List<Member> moderators = jdaGuild.getMembers().stream()
          .filter(m -> !m.getUser().isBot())
          .filter(m -> !m.getPermissions().containsAll(ADMINISTRATOR_PERMISSIONS))
          .filter(m -> !Collections.disjoint(m.getPermissions(), MODERATOR_PERMISSIONS))
          .collect(Collectors.toList());
  
    moderators.stream()
          .map(Member::getAsMention)
          .reduce((u, v) -> u + "\n" + v)
          .ifPresent(content -> builder.addField("Moderator(s)", content, false));
  
    // Add members
    long membersOnline = jdaGuild.getMembers().stream()
          .filter(e -> !e.getOnlineStatus().equals(OnlineStatus.OFFLINE))
          .count();
  
    long membersTotal = jdaGuild.getMembers().size();
  
    builder.addField("#Member(s)", String.format("%d / %d", membersOnline, membersTotal), true);
  
    // Add roles
    builder.addField("#Role(s)", Integer.toString(jdaGuild.getRoles().size()), true);
  
    // Add creation date
    builder.addField("Created", DATE.format(jdaGuild.getTimeCreated()), true);
  
    return builder.build();
  }
  
  /**
   * Creates an embedded message displaying the relevant information of the JDA role.
   *
   * @param jdaRole A JDA role.
   * @return A JDA message embed displaying the relevant role information.
   */
  public static MessageEmbed forRole(Role jdaRole) {
    EmbedBuilder builder = new EmbedBuilder();
  
    builder.setTitle(jdaRole.getName());
    builder.addField("ID", jdaRole.getId(), false);
  
    // Creation time
    Period period = Period.between(jdaRole.getTimeCreated().toLocalDate(), LocalDate.now());
    String message = String.format(
          "%s\n(%d %s, %d %s and %d %s ago)",
          DATE.format(jdaRole.getTimeCreated()),
          period.getYears(), "year(s)",
          period.getMonths(), "month(s)",
          period.getDays(), "day(s)"
    );
  
    builder.addField("Created", message, false);
  
    builder.addField("Position", Integer.toString(jdaRole.getPosition()), false);
    builder.addField("#Members", Integer.toString(jdaRole.getGuild().getMembersWithRoles(jdaRole).size()), false);
  
    // Color
    @Nullable Color color = jdaRole.getColor();
  
    if (color != null) {
      builder.addField("Color", String.format("0x%02X%02X%02X",
            color.getRed(),
            color.getGreen(),
            color.getBlue()),
            false
      );
    }
  
    return builder.build();
  }
  
  /**
   * Creates an embedded message displaying the relevant information of the Reddit link.
   *
   * @param link A Reddit link.
   * @return A JDA message embed displaying the relevant link information.
   */
  public static Message forLink(LinkValueObject link) {
    String qualifiedTitle = (link.getLinkFlairText() != null ? "[" + link.getLinkFlairText() + "]" : StringUtils.EMPTY)
          + (link.getOver18() ? "[NSFW] " : StringUtils.EMPTY)
          + (link.getSpoiler() ? "[Spoiler] " : StringUtils.EMPTY)
          + link.getTitle();
    qualifiedTitle = StringUtils.truncate(qualifiedTitle, TITLE_MAX_LENGTH);
    
    @Nullable String permalink = "https://www.reddit.com" + link.getPermalink();
    permalink = (permalink.length() < URL_MAX_LENGTH) ? permalink : null;
  
    @Nullable String url = link.getUrl();
    url = (url != null && url.length() < URL_MAX_LENGTH) ? url : null;
    
    @Nullable String thumbnail = link.getThumbnail();
    thumbnail = (thumbnail != null && thumbnail.length() < URL_MAX_LENGTH) ? thumbnail : null;
    
    @Nullable String description = link.getSelftext();
    description = StringUtils.truncate(description, DESCRIPTION_MAX_LENGTH);
  
    EmbedBuilder builder = new EmbedBuilder();
    
    builder.setTitle(qualifiedTitle, permalink);
    builder.setAuthor("source", url);
  
    if (link.getCreated() != null) {
      builder.setTimestamp(Instant.ofEpochSecond(link.getCreated().longValue()));
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
    MessageEmbed embed = builder.build();
  
    MessageBuilder messageBuilder = new MessageBuilder();
    messageBuilder.setEmbeds(embed);
    messageBuilder.setContent(String.format("New submission from u/%s in `r/%s`:\n\n<%s>", link.getAuthor(), link.getName(), shortlink));
  
    return messageBuilder.build();
  }
}
