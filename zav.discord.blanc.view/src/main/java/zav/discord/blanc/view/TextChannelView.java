package zav.discord.blanc.view;

public interface TextChannelView extends MessageChannelView {
  void updateSubreddit(String subreddit);
  WebhookView getWebhook(boolean create);
  default WebhookView getWebhook() {
    return getWebhook(false);
  }
}
