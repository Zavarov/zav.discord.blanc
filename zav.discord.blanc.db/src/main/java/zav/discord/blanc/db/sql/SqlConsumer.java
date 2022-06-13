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

package zav.discord.blanc.db.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * This interface should be implemented by every class that modifies an SQL database.
 * It is heavily inspired by the {@link Consumer} interface, with the only exception being that the
 * {@code accept} method is allowed to throw an {@link SQLException}.
 *
 * @see Consumer
 */
@FunctionalInterface
public interface SqlConsumer {
  void accept(PreparedStatement stmt) throws SQLException;
}
