package zav.discord.blanc.view;

import zav.discord.blanc.databind.WebHook;
import zav.jrc.databind.Link;

public interface WebHookView {
  // Databind
  WebHook getAbout();
  // Misc
  void updateSubreddit(String subreddit);
  void delete();
  void send(Link link);
}
