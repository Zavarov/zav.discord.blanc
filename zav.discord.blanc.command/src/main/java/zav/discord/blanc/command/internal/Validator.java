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

package zav.discord.blanc.command.internal;

import java.util.Collection;

/**
 * This interface is used in combination with commands in order to ensure that only users with
 * sufficient authorization are able to execute them.<br>
 * <pre>
 * Example:
 *   - Discord Permissions (Administrator, Manage Messages, ...)
 *   - Ranks (Root, Developer, ...)
 * </pre>
 *
 * @param <T> The type of authorization.
 */
public interface Validator<T> {
  void validate(Collection<T> args) throws Exception;
}
