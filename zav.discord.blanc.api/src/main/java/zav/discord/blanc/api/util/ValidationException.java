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

package zav.discord.blanc.api.util;

import java.util.concurrent.ExecutionException;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * This exception is thrown whenever a user tries to executes a command with
 * insufficient authorization.<br>
 * Here a descriptive error message should be returned, instead of just the
 * normal stack trace.
 */
public abstract class ValidationException extends ExecutionException {
  private static final long serialVersionUID = 8927239723989071327L;
  
  /**
   * Constructs a human-readable error message which can be send as a response via
   * the Discord API.
   *
   * @return A Discord message containing the error message.
   */
  public abstract MessageEmbed getErrorMessage();
}
