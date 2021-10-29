package zav.discord.blanc.view;

import zav.discord.blanc.databind.User;

public interface UserView {
  User getAbout();
  PrivateChannelView getPrivateChannel();
}
