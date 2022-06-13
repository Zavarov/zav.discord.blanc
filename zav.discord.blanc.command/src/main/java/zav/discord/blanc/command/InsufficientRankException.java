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

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.StringUtils;
import zav.discord.blanc.api.Rank;

/**
 * This exception is thrown whenever a user executes a command for which they lack the required
 * rank.
 *
 * @see Rank
 */
public class InsufficientRankException extends ExecutionException {
  public InsufficientRankException(Collection<Rank> ranks) {
    super(getMessage(ranks));
  }
  
  private static String getMessage(Collection<Rank> ranks) {
    return "You require the following rank(s) to execute this command: "
          + StringUtils.join(ranks, ",");
  }
}
