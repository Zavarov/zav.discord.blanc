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

package zav.discord.blanc.command.internal.resolver;

import static zav.discord.blanc.command.internal.ArgumentUtils.resolveById;
import static zav.discord.blanc.command.internal.ArgumentUtils.resolveByName;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Parameter;

/**
 * Derives a text channel based on its (unique) id or name, in this order. In case multiple text
 * channels with the same name exist, none are selected.
 */
@NonNullByDefault
public class TextChannelResolver implements EntityResolver<TextChannel> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TextChannelResolver.class);
  
  /**
   * Attempts to resolve the text channel corresponding to the provided argument.<br>
   * A channel can either be determined by its (unique) name or by its id. In case multiple channels
   * with the same name are found (including upper/lower cases), an exception is thrown.
   *
   * @param message The received guild message.
   * @param parameter Either a channel name or id.
   * @return A text channel within the current guild.
   */
  @Override
  public @Nullable TextChannel apply(Parameter parameter, Message message) {
    Guild guild = message.getGuild();
    TextChannel textChannel = resolveById(parameter, guild::getTextChannelById);
  
    // A text channel with matching id was found
    if (textChannel != null) {
      return textChannel;
    }
  
    textChannel = resolveByName(parameter, name -> guild.getTextChannelsByName(name, true));
  
    // A text channel with matching name was found
    if (textChannel != null) {
      return textChannel;
    }
  
    LOGGER.error("No matching text channel for {} has been found.", parameter);
    return null;
  }
}
