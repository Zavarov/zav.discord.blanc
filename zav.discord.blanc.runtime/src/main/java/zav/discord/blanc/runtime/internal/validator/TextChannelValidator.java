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

package zav.discord.blanc.runtime.internal.validator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.databind.TextChannelEntity;

/**
 * This class checks whether the persisted text channels are still valid.<br>
 * Channels become invalid, if one of the following conditions are met:
 * <pre>
 *   - The channel has been deleted
 *   - The channel is no longer accessible by the program.
 * </pre>
 */
@Deprecated
public class TextChannelValidator implements Validator<TextChannelEntity> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TextChannelValidator.class);
  private final Guild guild;
  
  /**
   * Creates a new instance of this class.
   *
   * @param guild The guild managed by this validator.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public TextChannelValidator(Guild guild) {
    this.guild = guild;
  }
  
  @Override
  public boolean test(TextChannelEntity entity) {
    TextChannel textChannel = guild.getTextChannelById(entity.getId());
    
    if (textChannel == null) {
      LOGGER.error("Invalid textchannel {0}: It doesn't exist.", entity.getName());
      return true;
    }
    
    if (!textChannel.canTalk()) {
      LOGGER.error("Invalid textchannel {0}: Inaccessible.", entity.getName());
      return true;
    }
    
    return false;
  }

}
