package zav.discord.blanc.view;

import zav.discord.blanc.Rank;
import zav.discord.blanc.databind.User;

import java.util.Set;
import java.util.stream.Collectors;

public interface UserView {
  User getAbout();
  PrivateChannelView getPrivateChannel();
  default Set<Rank> getRanks() {
    return getAbout().getRank().stream().map(Rank::valueOf).collect(Collectors.toUnmodifiableSet());
  }
}
