package zav.discord.blanc.view;

import zav.discord.blanc.Argument;
import zav.discord.blanc.databind.TextChannelValueObject;

/**
 * Base interface for all functions that are performed on text channels.
 */
public interface TextChannelView extends MessageChannelView {
  
  TextChannelValueObject getAbout();
  
  @Override
  GuildMessageView getMessage(Argument argument);
  
  WebHookView getWebhook(String argument, boolean create);
  
  default WebHookView getWebhook(String argument) {
    return getWebhook(argument, false);
  }
}
