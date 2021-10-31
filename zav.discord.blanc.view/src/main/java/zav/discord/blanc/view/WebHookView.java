package zav.discord.blanc.view;

import zav.discord.blanc.databind.WebHook;

public interface WebHookView {
  // Databind
  WebHook getAbout();
  // Misc
  void updateSubreddit(String subreddit);
  void delete();
}
