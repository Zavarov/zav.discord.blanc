package zav.discord.blanc.view;

import java.util.Collection;

public interface ShardView {
  Collection<GuildView> getAllGuilds();
  GuildView getGuild(long id);
  UserView getUser(long id);
}
