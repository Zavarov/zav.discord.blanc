package zav.discord.blanc.db.internal;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface SqlConsumer {
  void accept(PreparedStatement stmt) throws SQLException;
}
