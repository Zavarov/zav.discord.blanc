package zav.discord.blanc.view;

import zav.discord.blanc.databind.User;

import java.awt.image.BufferedImage;

public interface SelfUserView extends UserView {
  // Databind
  User getAbout();
  // Misc
  void setAvatar(BufferedImage image);
}
