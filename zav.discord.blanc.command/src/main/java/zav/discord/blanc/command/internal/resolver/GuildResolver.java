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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.Parameter;
import zav.discord.blanc.command.internal.ArgumentUtils;

/**
 * Derives a guild based on its (unique) id or name, in this order. In case multiple guilds with the
 * same name exist, none are selected.
 */
@NonNullByDefault
public final class GuildResolver implements EntityResolver<Guild> {
  private static final Logger LOGGER = LogManager.getLogger(GuildResolver.class);
  
  /**
   * Attempts to resolve the guild corresponding to the provided argument.<br>
   * A guild can either be determined by its (unique) name or by its id. In case multiple guilds
   * with the same name are found (including upper/lower cases), an exception is thrown.
   *
   * @param message The received message.
   * @param parameter Either a guild name or id.
   * @return A guild within the provided shard.
   */
  @Override
  public @Nullable Guild apply(Parameter parameter, Message message) {
    JDA jda = message.getJDA();
    Guild jdaGuild = ArgumentUtils.resolveById(parameter, jda::getGuildById);
  
    // A guild with matching id was found
    if (jdaGuild != null) {
      return jdaGuild;
    }
  
    jdaGuild = ArgumentUtils.resolveByName(parameter, name -> jda.getGuildsByName(name, true));
  
    // A unique guild matching this name has been found
    if (jdaGuild != null) {
      return jdaGuild;
    }
  
    LOGGER.error("No matching guild for {} has been found.", parameter);
    return null;
  }
}
