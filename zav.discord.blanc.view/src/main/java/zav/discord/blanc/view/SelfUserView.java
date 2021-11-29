package zav.discord.blanc.view;

import java.awt.image.BufferedImage;
import zav.discord.blanc.databind.UserValueObject;

/**
 * Base interface for all functions that are performed on this application.
 */
public interface SelfUserView extends UserView {
  
  UserValueObject getAbout();

  void setAvatar(BufferedImage image);
}
