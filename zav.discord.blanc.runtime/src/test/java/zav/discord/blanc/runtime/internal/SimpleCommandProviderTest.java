package zav.discord.blanc.runtime.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.CommandProvider;
import zav.discord.blanc.runtime.command.AbstractTest;
import zav.discord.blanc.runtime.command.core.MathCommand;
import zav.discord.blanc.runtime.command.core.SupportCommand;
import zav.discord.blanc.runtime.command.dev.FailsafeCommand;
import zav.discord.blanc.runtime.command.dev.KillCommand;
import zav.discord.blanc.runtime.command.dev.SayCommand;
import zav.discord.blanc.runtime.command.dev.StatusCommand;
import zav.discord.blanc.runtime.command.mod.BlacklistAddCommand;
import zav.discord.blanc.runtime.command.mod.BlacklistInfoCommand;
import zav.discord.blanc.runtime.command.mod.BlacklistRemoveCommand;
import zav.discord.blanc.runtime.command.mod.LegacyRedditInfoCommand;
import zav.discord.blanc.runtime.command.mod.LegacyRedditRemoveCommand;
import zav.discord.blanc.runtime.command.mod.RedditAddCommand;
import zav.discord.blanc.runtime.command.mod.RedditInfoCommand;
import zav.discord.blanc.runtime.command.mod.RedditRemoveCommand;
import zav.discord.blanc.runtime.command.mod.ResponseAddCommand;
import zav.discord.blanc.runtime.command.mod.ResponseInfoCommand;
import zav.discord.blanc.runtime.command.mod.ResponseRemoveCommand;

/**
 * Checks whether the commands are mapped to their correct names.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("deprecation")
public class SimpleCommandProviderTest extends AbstractTest {
  CommandProvider provider;
  @Mock OptionMapping value;
  
  /**
   * Initializes the provider as well as all parameters which are required by at least one command.
   */
  @BeforeEach
  public void setUp() {
    provider = new SimpleCommandProvider();
    
    lenient().when(event.getOption("content")).thenReturn(value);
    lenient().when(event.getOption("value")).thenReturn(value);
  }
  
  private void setName(String name) {
    String[] parts = name.split("\\.");
    
    when(event.getName()).thenReturn(parts[0]);
    
    if (parts.length > 1) {
      when(event.getSubcommandGroup()).thenReturn(parts[1]);
    }
    
    if (parts.length > 2) {
      when(event.getSubcommandName()).thenReturn(parts[2]);
    }
  }
  
  /**
   * All commands which can be created within a guild.
   *
   * @return A stream of all test parameters.
   */
  public static Stream<Arguments> testCreateGuildCommand() {
    return Stream.of(
        Arguments.of("math", MathCommand.class),
        Arguments.of("support", SupportCommand.class),
        Arguments.of("dev.failsafe", FailsafeCommand.class),
        Arguments.of("dev.kill", KillCommand.class),
        Arguments.of("dev.say", SayCommand.class),
        Arguments.of("dev.status", StatusCommand.class),
        Arguments.of("mod.blacklist.add", BlacklistAddCommand.class),
        Arguments.of("mod.blacklist.remove", BlacklistRemoveCommand.class),
        Arguments.of("mod.blacklist.info", BlacklistInfoCommand.class),
        Arguments.of("mod.reddit.add", RedditAddCommand.class),
        Arguments.of("mod.reddit.remove", RedditRemoveCommand.class),
        Arguments.of("mod.reddit.info", RedditInfoCommand.class),
        Arguments.of("mod.reddit_legacy.remove", LegacyRedditRemoveCommand.class),
        Arguments.of("mod.reddit_legacy.info", LegacyRedditInfoCommand.class),
        Arguments.of("mod.auto-response.add", ResponseAddCommand.class),
        Arguments.of("mod.auto-response.remove", ResponseRemoveCommand.class),
        Arguments.of("mod.auto-response.info", ResponseInfoCommand.class)
    );
  }
  
  /**
   * Checks whether the command corresponding to the given name is of the expected type.
   *
   * @param name The command name.
   * @param clazz The expected type of command.
   */
  @MethodSource
  @ParameterizedTest
  public void testCreateGuildCommand(String name, Class<?> clazz) {
    when(event.isFromGuild()).thenReturn(true);
    
    setName(name);
    
    assertEquals(provider.create(shard, event).orElseThrow().getClass(), clazz);
  }
  
  /**
   * All commands which can be created outside a guild.
   *
   * @return A stream of all test parameters.
   */
  public static Stream<Arguments> testCreateCommand() {
    return Stream.of(
        Arguments.of("math", MathCommand.class),
        Arguments.of("support", SupportCommand.class),
        Arguments.of("dev.failsafe", FailsafeCommand.class),
        Arguments.of("dev.kill", KillCommand.class),
        Arguments.of("dev.say", SayCommand.class),
        Arguments.of("dev.status", StatusCommand.class)
    );
  }
  
  /**
   * Checks whether the command corresponding to the given name is of the expected type.
   *
   * @param name The command name.
   * @param clazz The expected type of command.
   */
  @MethodSource
  @ParameterizedTest
  public void testCreateCommand(String name, Class<?> clazz) {
    when(event.isFromGuild()).thenReturn(false);
    
    setName(name);
    
    assertEquals(provider.create(shard, event).orElseThrow().getClass(), clazz);
  }
  
  @Test
  public void testCreateUnknownCommand() {
    setName("foo.bar");
    
    assertTrue(provider.create(shard, event).isEmpty());
  }
}
