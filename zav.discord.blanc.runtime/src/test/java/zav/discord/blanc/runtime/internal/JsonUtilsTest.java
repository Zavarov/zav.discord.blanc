package zav.discord.blanc.runtime.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import zav.discord.blanc.databind.Credentials;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.databind.WebhookEntity;

/**
 * This test case checks whether all entities can be properly deserialized.
 */
public class JsonUtilsTest {
  
  @MethodSource
  @ParameterizedTest
  public void testRead(String fileName, Class<?> clazz) throws IOException {
    assertNotNull(JsonUtils.read(fileName, clazz));
  }
  
  /**
   * Provides the arguments for {@link #testRead(String, Class)}. The first argument is the file
   * path to the entity, the second the expected class.
   *
   * @return The arguments for {@link #testRead(String, Class)}.
   */
  public static Stream<Arguments> testRead() {
    return Stream.of(
        Arguments.of("src/test/resources/Credentials.json", Credentials.class),
        Arguments.of("src/test/resources/Guild.json", GuildEntity.class),
        Arguments.of("src/test/resources/User.json", UserEntity.class),
        Arguments.of("src/test/resources/Webhook.json", WebhookEntity.class),
        Arguments.of("src/test/resources/TextChannel.json", TextChannelEntity.class)
    );
  }
  
  @Test
  public void testGetCommands() {
    List<CommandData> commands = JsonUtils.getCommands();
    
    // Should match the content of the JSON files
    assertEquals(commands.size(), 4);
    assertEquals(commands.get(0).getName(), "math");
    assertEquals(commands.get(1).getName(), "support");
    assertEquals(commands.get(2).getName(), "dev");
    assertEquals(commands.get(2).getSubcommands().size(), 4);
    assertEquals(commands.get(3).getName(), "mod");
    assertEquals(commands.get(3).getSubcommands().size(), 0);
    assertEquals(commands.get(3).getSubcommandGroups().size(), 3);
  }
}
