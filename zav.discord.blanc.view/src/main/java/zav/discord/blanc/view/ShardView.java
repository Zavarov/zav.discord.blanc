package zav.discord.blanc.view;

import zav.discord.blanc.Shard;

import java.util.Collection;

public interface ShardView {
  void shutdown();
  Shard getAbout();
  SelfUserView getSelfUser();
  Collection<GuildView> getGuilds();
  GuildView getGuild(long id);
  UserView getUser(long id);
}
