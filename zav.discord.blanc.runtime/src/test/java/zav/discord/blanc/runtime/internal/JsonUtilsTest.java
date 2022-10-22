package zav.discord.blanc.runtime.internal;

import static org.assertj.core.api.Assertions.assertThat;
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
    assertThat(commands).hasSize(4);
    assertThat(commands.get(0).getName()).isEqualTo("math");
    assertThat(commands.get(1).getName()).isEqualTo("support");
    assertThat(commands.get(2).getName()).isEqualTo("dev");
    assertThat(commands.get(2).getSubcommands()).hasSize(4);
    assertThat(commands.get(3).getName()).isEqualTo("mod");
    assertThat(commands.get(3).getSubcommands()).hasSize(0);
    assertThat(commands.get(3).getSubcommandGroups()).hasSize(3);
  }
}
