package zav.discord.blanc.command.parser;

import zav.discord.blanc.Argument;

import java.util.Optional;

public interface NumberArgument extends Argument {
  @Override
  default Optional<String> asString() {
    return asNumber().map(Number::toString);
  }
}
