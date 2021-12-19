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

import static zav.discord.blanc.jda.internal.ImageUtils.asIcon;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.dv8tion.jda.api.entities.Icon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.api.SelfMember;

/**
 * Implementation of a self-member view, backed by JDA.
 */
public class JdaSelfMember extends JdaMember implements SelfMember {
  private static final Logger LOGGER = LogManager.getLogger(JdaSelfMember.class);
  
  @Override
  public void setNickname(String nickname) {
    jdaMember.modifyNickname(nickname).complete();
  }
  
  @Override
  public void setAvatar(BufferedImage image) {
    try {
      jdaMember.getJDA().getSelfUser().getManager().setAvatar(asIcon(image)).complete();
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
