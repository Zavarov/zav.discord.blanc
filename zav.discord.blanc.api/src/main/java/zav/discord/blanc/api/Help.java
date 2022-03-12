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

package zav.discord.blanc.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Provides the user-help for commands. Can be triggered by calling a command with the {@code -h}
 * flag.
 */
public interface Help {
  /**
   * Reads the help file corresponding to this class from disc. The file is located in the
   * {@code help} directory, located in the root classpath. The file name is the chanonical class
   * name. The file ending is {@code md}.
   *
   * @return A message embed containing the help for this class.
   * @throws IOException If the help file couldn't be read.
   */
  default MessageEmbed getHelp() throws IOException {
    String fileName = getClass().getCanonicalName() + ".md";
    String filePath = "help/" + fileName;
    
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
      Objects.requireNonNull(is);
      
      String source = new String(is.readAllBytes(), StandardCharsets.UTF_8);
  
      return new EmbedBuilder().setDescription(source).build();
    }
  }
}
