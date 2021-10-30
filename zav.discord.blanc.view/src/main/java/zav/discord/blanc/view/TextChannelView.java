package zav.discord.blanc.view;

public interface TextChannelView extends MessageChannelView {
  @Override
  GuildMessageView getMessage(long id);
  void updateSubreddit(String subreddit);
  WebhookView getWebhook(boolean create);
  default WebhookView getWebhook() {
    return getWebhook(false);
  }
}
