package zav.discord.blanc.view;

import zav.discord.blanc.databind.UserValueObject;

/**
 * Base interface for all functions that are performed on users.
 */
public interface UserView {
  
  UserValueObject getAbout();
}
