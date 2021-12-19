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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.dv8tion.jda.api.entities.Icon;

/**
 * Utility class for transforming images into a JDA readable format.
 */
public final class ImageUtils {
  
  /**
   * Transforms the image into a JDA icon.<br>
   * May be used when updating a user avatar.
   *
   * @param image An image.
   * @return A JDA icon.
   * @throws IOException in case the image couldn't be converted into an icon.
   */
  public static Icon asIcon(BufferedImage image) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ImageIO.write(image, "png", os);
    return Icon.from(os.toByteArray());
  }
  
  /**
   * Transforms the image into an input stream.<br>
   * May be used when sending images over message channels.
   *
   * @param image An image.
   * @return An input stream.
   * @throws IOException in case the image couldn't be converted into an input stream.
   */
  public static InputStream asInputStream(BufferedImage image) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ImageIO.write(image, "png", os);
    return new ByteArrayInputStream(os.toByteArray());
  }
}
