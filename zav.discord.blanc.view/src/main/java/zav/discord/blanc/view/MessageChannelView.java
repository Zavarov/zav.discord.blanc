package zav.discord.blanc.view;

import java.awt.image.BufferedImage;
import zav.discord.blanc.Argument;
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.databind.RoleValueObject;
import zav.discord.blanc.databind.UserValueObject;
import zav.jrc.databind.Link;

/**
 * Base interface for all functions that are performed in message channels.<br>
 * This includes both private and guild channels.
 */
public interface MessageChannelView {
  
  MessageView getMessage(Argument argument);
  
  default void send(String format, Object... args) {
    send(String.format(format, args));
  }
  
  void send(BufferedImage image, String imageName);
  
  void send(Object content);
  
  void send(GuildValueObject guild);
  
  void send(RoleValueObject role);
  
  void send(UserValueObject user);
  
  void send(Link link);
}
