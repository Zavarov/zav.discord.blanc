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

package zav.discord.blanc.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import zav.discord.blanc.databind.GuildDto;
import zav.discord.blanc.databind.TextChannelDto;
import zav.discord.blanc.databind.UserDto;
import zav.discord.blanc.databind.WebHookDto;

/**
 * Base class for all test suites.<br>
 * Initializes all databases.
 */
@SuppressWarnings("NotNullFieldNotInitialized")
public abstract class AbstractTest {
  private static final Path GUILD_DB = Paths.get("Guild.db");
  private static final Path CHANNEL_DB = Paths.get("TextChannel.db");
  private static final Path WEBHOOK_DB = Paths.get("WebHook.db");
  private static final Path USER_DB = Paths.get("User.db");
  
  private static final Path RESOURCES = Paths.get("src/test/resources");
  
  protected GuildDto guild;
  protected TextChannelDto channel;
  protected WebHookDto hook;
  protected UserDto user;
  
  /**
   * Deserializes Discord instances.
   *
   * @throws SQLException If a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    guild = read("Guild.json", GuildDto.class);
    channel = read("TextChannel.json", TextChannelDto.class);
    hook = read("WebHook.json", WebHookDto.class);
    user = read("User.json", UserDto.class);
  }
  
  /**
   * Delete all database files.
   *
   * @throws IOException If one of the databases couldn't be deleted.
   */
  @AfterEach
  public void cleanUp() throws IOException {
    delete(GUILD_DB);
    delete(CHANNEL_DB);
    delete(WEBHOOK_DB);
    delete(USER_DB);
  }
  
  private void delete(Path db) throws IOException {
    if (Files.exists(db)) {
      Files.delete(db);
    }
  }
  
  /**
   * Deserializes the file with the specified name.
   *
   * @param fileName The file name of the serialized class.
   * @param clazz The desired target class.
   * @param <T> The desired class type.
   * @return A deserialized instance.
   */
  public static <T> T read(String fileName, Class<T> clazz) {
    try {
      ObjectMapper om = new ObjectMapper();
      return om.readValue(RESOURCES.resolve(fileName).toFile(), clazz);
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }
}
