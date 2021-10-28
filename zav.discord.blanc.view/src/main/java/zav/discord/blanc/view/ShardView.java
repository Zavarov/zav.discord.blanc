package zav.discord.blanc.view;

import java.util.Collection;

public interface ShardView {
  Collection<GuildView> getGuilds();
  GuildView getGuild(long id);
  UserView getUser(long id);
}
