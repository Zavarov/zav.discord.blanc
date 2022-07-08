package zav.discord.blanc.runtime.internal;

import com.google.inject.AbstractModule;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import zav.discord.blanc.api.CommandParser;
import zav.discord.blanc.command.GuiceCommandParser;
import zav.discord.blanc.databind.Credentials;

/**
 * The Guice module of the entire client.
 */
public class BlancModule extends AbstractModule {
  private final Credentials credentials;
  private final ScheduledExecutorService pool;

  /**
   * Creates a new instance of this class.
   *
   * @param credentials The configuration file.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public BlancModule(Credentials credentials) {
    this.credentials = credentials;
    this.pool = Executors.newScheduledThreadPool(8);
  }
  
  @Override
  public void configure() {
    bind(Credentials.class).toInstance(credentials);
    bind(ScheduledExecutorService.class).toInstance(pool);
    bind(CommandParser.class).to(GuiceCommandParser.class);
  }
}
