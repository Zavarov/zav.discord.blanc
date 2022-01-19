package zav.discord.blanc.command.parser;

import java.math.BigDecimal;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Parameter;

/**
 * This interface is for arguments that only have a string representation.<br>
 * It implements {@link Parameter#asNumber()} using {@link BigDecimal#BigDecimal(String)}. If the
 * string can't be converted into a number, {@link Optional#empty()} is returned.
 */
public interface StringParameter extends Parameter {
  Logger LOGGER = LogManager.getLogger(StringParameter.class);
  @Override
  @Contract(pure = true)
  default Optional<BigDecimal> asNumber() {
    try {
      return asString().map(BigDecimal::new);
    } catch (NumberFormatException e) {
      LOGGER.debug(e.getMessage(), e);
      return Optional.empty();
    }
  }
}
