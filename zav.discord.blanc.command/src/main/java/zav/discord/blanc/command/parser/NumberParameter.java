package zav.discord.blanc.command.parser;

import java.util.Optional;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Parameter;

/**
 * This interface is for arguments that only have a number representation.<br>
 * It implements {@link Parameter#asString()} using {@link Number#toString()}.
 */
@NonNullByDefault
public interface NumberParameter extends Parameter {
  @Override
  @Contract(pure = true)
  default Optional<String> asString() {
    return asNumber().map(Number::toString);
  }
}
