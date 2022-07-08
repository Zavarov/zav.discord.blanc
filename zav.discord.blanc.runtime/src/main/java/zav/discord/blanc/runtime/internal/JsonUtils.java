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
}
