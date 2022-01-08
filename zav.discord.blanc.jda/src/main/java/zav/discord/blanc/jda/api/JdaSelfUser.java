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

package zav.discord.blanc.jda.api;

import static zav.discord.blanc.jda.internal.DatabaseUtils.aboutSelfUser;
import static zav.discord.blanc.jda.internal.ImageUtils.asIcon;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.SelfUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.databind.UserDto;

/**
 * Implementation of a self-user view, backed by JDA.
 */
public class JdaSelfUser implements zav.discord.blanc.api.SelfUser {
  private static final Logger LOGGER = LogManager.getLogger(JdaSelfUser.class);

  @Inject
  protected SelfUser jdaSelfUser;
  
  @Override
  public UserDto getAbout() {
    return aboutSelfUser(jdaSelfUser);
  }
  
  @Override
  public String getAsMention() {
    return jdaSelfUser.getAsMention();
  }
  
  @Override
  public void setAvatar(BufferedImage image) {
    try {
      jdaSelfUser.getManager().setAvatar(asIcon(image)).complete();
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
