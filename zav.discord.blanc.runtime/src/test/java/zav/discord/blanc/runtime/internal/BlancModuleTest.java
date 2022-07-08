package zav.discord.blanc.runtime.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import zav.discord.blanc.api.CommandParser;
import zav.discord.blanc.databind.Credentials;

/**
 * This test case checks whether all instances bound to the root module can be instantiated.
 */
public class BlancModuleTest {
  Credentials credentials;
  Injector guice;
  
  static {
    System.setProperty("org.jboss.logging.provider", "slf4j");
  }
  
  @BeforeEach
  public void setUp() {
    credentials = new Credentials();
    guice = Guice.createInjector(new BlancModule(credentials));
  }
  
  @MethodSource
  @ParameterizedTest
  public void testGetInstance(Class<?> clazz) {
    assertNotNull(guice.getInstance(clazz));
  }
  
  public static Stream<Class<?>> testGetInstance() {
    return Stream.of(CommandParser.class, ScheduledExecutorService.class, Credentials.class);
  }
}
