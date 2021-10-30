package zav.discord.blanc.view;

import zav.discord.blanc.Argument;
import zav.discord.blanc.databind.TextChannel;

public interface TextChannelView extends MessageChannelView {
  // Databind
  TextChannel getAbout();
  // Views
  @Override
  GuildMessageView getMessage(Argument argument);
  WebHookView getWebhook(Argument argument, boolean create);
  default WebHookView getWebhook(Argument argument) {
    return getWebhook(argument, false);
  }
  // Misc
  void updateSubreddit(String subreddit);
}
