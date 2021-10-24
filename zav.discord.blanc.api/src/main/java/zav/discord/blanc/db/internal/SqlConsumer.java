package zav.discord.blanc.db.internal;

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
