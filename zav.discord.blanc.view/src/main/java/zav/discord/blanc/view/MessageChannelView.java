package zav.discord.blanc.view;

import zav.discord.blanc.Argument;
import zav.discord.blanc.databind.User;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;

public interface MessageChannelView {
  // Views
  MessageView getMessage(Argument argument);
  // Misc
  default void send(String format, Object... args) {
    send(String.format(format, args));
  }
  void send(BufferedImage image, String imageName);
  void send(Object content);
  void send(User user);
}
