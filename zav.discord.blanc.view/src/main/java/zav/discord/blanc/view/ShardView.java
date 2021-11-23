package zav.discord.blanc.view;

import zav.discord.blanc.Argument;
import zav.discord.blanc.Shard;

import java.util.Collection;

public interface ShardView {
  // Databind
  Shard getAbout();
  // Views
  Collection<GuildView> getGuilds();
  SelfUserView getSelfUser();
  GuildView getGuild(Argument argument);
  UserView getUser(Argument argument);
  // Misc
  void shutdown();
  <T extends Runnable> void submit(T job);
}
