package zav.discord.blanc.view;

import java.util.Collection;
import zav.discord.blanc.Argument;
import zav.discord.blanc.Shard;

/**
 * Base interface for all functions that are performed on this application.
 */
public interface ShardView {
  
  Shard getAbout();
  
  /**
   * Returns a view over all guilds in this shard.
   *
   * @return An immutable list of guild views.
   */
  Collection<GuildView> getGuilds();
  
  /**
   * Returns the Discord user corresponding to this application.
   *
   * @return A user view over this application.
   */
  SelfUserView getSelfUser();
  
  GuildView getGuild(Argument argument);
  
  UserView getUser(Argument argument);
  
  void shutdown();
  
  <T extends Runnable> void submit(T job);
}
