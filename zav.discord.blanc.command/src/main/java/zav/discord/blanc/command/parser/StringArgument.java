package zav.discord.blanc.command.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.Argument;

import java.math.BigDecimal;
import java.util.Optional;

public interface StringArgument extends Argument {
  Logger LOGGER = LogManager.getLogger(StringArgument.class);
  @Override
  default Optional<BigDecimal> asNumber() {
    try {
      return asString().map(BigDecimal::new);
    } catch(NumberFormatException e) {
      LOGGER.warn(e.getMessage(), e);
      return Optional.empty();
    }
  }
}
