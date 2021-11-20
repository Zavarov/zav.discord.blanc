package zav.discord.blanc.command.parser;

import java.math.BigDecimal;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.Argument;

/**
 * This interface is for arguments that only have a string representation.<br>
 * It implements {@link Argument#asNumber()} using {@link BigDecimal#BigDecimal(String)}. If the
 * string can't be converted into a number, {@link Optional#empty()} is returned.
 */
public interface StringArgument extends Argument {
  Logger LOGGER = LogManager.getLogger(StringArgument.class);
  @Override
  default Optional<BigDecimal> asNumber() {
    try {
      return asString().map(BigDecimal::new);
    } catch (NumberFormatException e) {
      LOGGER.warn(e.getMessage(), e);
      return Optional.empty();
    }
  }
}
