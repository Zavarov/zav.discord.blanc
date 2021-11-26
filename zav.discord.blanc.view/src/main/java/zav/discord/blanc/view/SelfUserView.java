package zav.discord.blanc.view;

import java.awt.image.BufferedImage;
import zav.discord.blanc.databind.User;

/**
 * Base interface for all functions that are performed on this application.
 */
public interface SelfUserView extends UserView {

  User getAbout();

  void setAvatar(BufferedImage image);
}
