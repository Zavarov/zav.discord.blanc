package zav.discord.blanc.api;

import java.util.List;

/**
 * Interface for commands returning interactive messages.
 */
public interface RichResponse {
  /**
   * A list of all pages that make up the implementing command.
   * 
   * @return A list of interactive messages.
   */
  List<Site.Page> getPages();
}
