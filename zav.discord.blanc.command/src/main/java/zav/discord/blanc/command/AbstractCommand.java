/*
 * Copyright (c) 2022 Zavarov.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package zav.discord.blanc.command;

import static zav.discord.blanc.api.Rank.USER;

import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.internal.RankValidator;

/**
 * Abstract base class for all commands.<br>
 * Commands can be either executed in a guild or private channel.
 */
public abstract class AbstractCommand implements Command {
  protected final ResourceBundle i18n;
  
  private @Nullable RankValidator validator;
  
  private final Rank requiredRank;
  
  protected AbstractCommand(Rank requiredRank) {
    this.requiredRank = requiredRank;
    this.i18n = ResourceBundle.getBundle("i18n");
  }
  
  protected AbstractCommand() {
    this(USER);
  }
  
  @Inject
  /*package*/ void setValidator(RankValidator validator) {
    this.validator = validator;
  }
  
  @Override
  @Contract(pure = true)
  public void validate() throws ExecutionException {
    Objects.requireNonNull(validator);
    // Does the user have the required rank?
    validator.validate(List.of(requiredRank));
  }
}
