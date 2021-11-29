package zav.discord.blanc.view;

import zav.discord.blanc.databind.WebHookValueObject;
import zav.jrc.databind.Link;

/**
 * Base interface for all functions that are performed on web hooks.
 */
public interface WebHookView {
  WebHookValueObject getAbout();
  
  void delete();
  
  void send(Link link);
}
