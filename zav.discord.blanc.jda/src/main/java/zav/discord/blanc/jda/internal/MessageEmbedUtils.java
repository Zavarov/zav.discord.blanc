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

import java.awt.Color;
import java.util.Date;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import zav.discord.blanc.databind.message.FieldValueObject;
import zav.discord.blanc.databind.message.MessageEmbedValueObject;
import zav.discord.blanc.databind.message.PageValueObject;

public final class MessageEmbedUtils {
  
  private MessageEmbedUtils() {
  
  }
  
  public static MessageEmbed forPage(PageValueObject page) {
    return forEmbed((MessageEmbedValueObject) page.getContent());
  }
  
  public static MessageEmbed forEmbed(MessageEmbedValueObject messageEmbed) {
    EmbedBuilder builder = new EmbedBuilder();
  
    messageEmbed.getThumbnail().ifPresent(builder::setThumbnail);
    messageEmbed.getContent().ifPresent(builder::setDescription);
    messageEmbed.getTimestamp().map(Date::toInstant).ifPresent(builder::setTimestamp);
    
    builder.setColor(messageEmbed.getColor().map(Color::getColor).orElse(Color.BLACK));
    
    messageEmbed.getTitle().ifPresent(title -> {
      builder.setTitle(title.getName(), title.getUrl().orElse(null));
    });
  
    messageEmbed.getAuthor().ifPresent(author -> {
      builder.setTitle(author.getName(), author.getUrl().orElse(null));
    });
  
    for (FieldValueObject field : messageEmbed.getFields()) {
      builder.addField(field.getName().toString(), field.getContent(), false);
    }
  
    return builder.build();
  }
}