package zav.discord.blanc.command.parser;

import java.math.BigDecimal;
import java.util.Optional;

public interface StringArgument extends Argument {
  @Override
  default Optional<BigDecimal> asNumber() {
    try {
      return asString().map(BigDecimal::new);
    } catch(NumberFormatException e) {
      return Optional.empty();
    }
  }
}
