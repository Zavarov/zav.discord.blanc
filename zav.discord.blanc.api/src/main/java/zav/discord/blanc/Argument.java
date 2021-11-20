package zav.discord.blanc;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * A generic argument of a command.<br>
 * An argument may either be a number (e.g. when providing a user id) or a plain string.
 */
public interface Argument {
  Optional<BigDecimal> asNumber();
  
  Optional<String> asString();
}
