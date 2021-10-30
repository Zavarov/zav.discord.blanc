package zav.discord.blanc.view;

import zav.discord.blanc.databind.User;

public interface UserView {
  // Databind
  User getAbout();
  // Views
  PrivateChannelView getPrivateChannel();
}
