package zav.discord.blanc.command.parser;

import java.math.BigDecimal;
import java.util.Optional;

public interface Argument {
  Optional<BigDecimal> asNumber();
  Optional<String> asString();
}
