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

package zav.discord.blanc.runtime.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

/**
 * Utility class for reading entities from disk and transforming them into Java objects.
 */
public final class JsonUtils {
  
  private JsonUtils() {}
  
  /**
   * Reads a JSON file and transforms it into a Java entity.<br>
   * The file has to be located in the root directory.
   *
   * @param fileName The name of the JSON file.
   * @param clazz The target class.
   * @param <T> The target type.
   * @return An instance of the target file.
   * @throws IOException If the file couldn't be read.
   */
  public static <T> T read(String fileName, Class<T> clazz) throws IOException {
    ObjectMapper om = new ObjectMapper();
    File file = new File(fileName);
    return om.readValue(file, clazz);
  }
  
  /**
   * Deserializes all available commands from disk.
   *
   * @return A list of all commands supported by the bot.
   */
  public static List<CommandData> getCommands() {
    ClassLoader cl = JsonUtils.class.getClassLoader();
    
    // Core Commands
    InputStream is = cl.getResourceAsStream("Commands.json");
    Objects.requireNonNull(is);
    List<CommandData> result = new ArrayList<>(CommandData.fromList(DataArray.fromJson(is)));
    
    // Developer Commands
    is = cl.getResourceAsStream("DeveloperCommands.json");
    Objects.requireNonNull(is);
    result.add(CommandData.fromData(DataObject.fromJson(is)));
    
    // Mod Commands
    is = cl.getResourceAsStream("ModCommands.json");
    Objects.requireNonNull(is);
    result.add(CommandData.fromData(DataObject.fromJson(is)));
    
    return result;
  }
}
