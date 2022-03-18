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

package zav.discord.blanc.api;

import java.util.concurrent.Callable;
import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * This interface should be implemented by any class that should be executed by an asynchronous
 * thread within the application.<br>
 * It is heavily inspired by both the {@link Callable} and {@link Runnable} interface.
 *
 * @see Runnable
 * @see Callable
 */
@NonNullByDefault
public interface Job {
  void run() throws Exception;
}
