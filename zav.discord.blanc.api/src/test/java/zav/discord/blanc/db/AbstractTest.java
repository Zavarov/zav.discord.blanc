package zav.discord.blanc.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import zav.discord.blanc.databind.Guild;
import zav.discord.blanc.databind.Role;
import zav.discord.blanc.databind.TextChannel;
import zav.discord.blanc.databind.User;
import zav.discord.blanc.databind.WebHook;

/**
 * Base class for all test suites.<br>
 * Initializes all databases.
 */
@SuppressWarnings("NotNullFieldNotInitialized")
public abstract class AbstractTest {
  private static final Path GUILD_DB = Paths.get("Guild.db");
  private static final Path ROLE_DB = Paths.get("Role.db");
  private static final Path CHANNEL_DB = Paths.get("TextChannel.db");
  private static final Path WEBHOOK_DB = Paths.get("WebHook.db");
  private static final Path USER_DB = Paths.get("User.db");
  
  private static final Path RESOURCES = Paths.get("src/test/resources");
  
  protected Guild guild;
  protected Role role;
  protected TextChannel channel;
  protected WebHook hook;
  protected User user;
  
  /**
   * Deserializes Discord instances.
   *
   * @throws SQLException If a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    guild = read("Guild.json", Guild.class);
    role = read("Role.json", Role.class);
    channel = read("TextChannel.json", TextChannel.class);
    hook = read("WebHook.json", WebHook.class);
    user = read("User.json", User.class);
  }
  
  /**
   * Delete all database files.
   *
   * @throws IOException If one of the databases couldn't be deleted.
   */
  @AfterEach
  public void cleanUp() throws IOException {
    delete(GUILD_DB);
    delete(ROLE_DB);
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
