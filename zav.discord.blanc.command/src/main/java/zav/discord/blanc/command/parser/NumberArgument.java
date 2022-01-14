package zav.discord.blanc.command.parser;

import java.util.Optional;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Argument;

/**
 * This interface is for arguments that only have a number representation.<br>
 * It implements {@link Argument#asString()} using {@link Number#toString()}.
 */
public interface NumberArgument extends Argument {
  @Override
  @Contract(pure = true)
  default Optional<String> asString() {
    return asNumber().map(Number::toString);
  }
}
