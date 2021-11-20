package zav.discord.blanc.command.parser;

import java.util.Optional;
import zav.discord.blanc.Argument;

/**
 * This interface is for arguments that only have a number representation.<br>
 * It implements {@link Argument#asString()} using {@link Number#toString()}.
 */
public interface NumberArgument extends Argument {
  @Override
  default Optional<String> asString() {
    return asNumber().map(Number::toString);
  }
}
