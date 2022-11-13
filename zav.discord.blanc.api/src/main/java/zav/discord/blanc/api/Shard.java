package zav.discord.blanc.api;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.dv8tion.jda.api.JDA;
import zav.discord.blanc.api.util.AbstractApplicationContext;

/**
 * A shard is a sub-component of the application. Each shard should be treated as an independent
 * component, which is unaware of any other shards.
 */
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class Shard extends AbstractApplicationContext {
  private final Client client;
  private final JDA jda;
  
  /**
   * Creates a new shard instance.
   *
   * @param client The application instance.
   * @param jda The JDA instance mapped to this shard.
   */
  public Shard(Client client, JDA jda) {
    this.client = client;
    this.jda = jda;
  }
  
  /**
   * Returns the application instance belonging to this shard.
   *
   * @return As described.
   */
  public Client getClient() {
    return client;
  }
  
  /**
   * Returns the JDA instance mapped to this shard.
   *
   * @return As described.
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public JDA getJda() {
    return jda;
  }
}
