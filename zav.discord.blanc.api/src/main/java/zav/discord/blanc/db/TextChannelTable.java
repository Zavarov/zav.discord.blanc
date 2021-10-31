package zav.discord.blanc.db;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import zav.discord.blanc.databind.Guild;
import zav.discord.blanc.databind.TextChannel;
import zav.discord.blanc.db.internal.SqlObject;
import zav.discord.blanc.db.internal.SqlQuery;

/**
 * Utility class for communicating with the {@code TextChannel} database.
 */
public abstract class TextChannelTable {
  private static final SqlQuery SQL = new SqlQuery(SqlQuery.TEXTCHANNEL_DB);
  private TextChannelTable() {}
  
  public static void create() throws SQLException {
    SQL.update("textchannel/CreateTextChannelTable.sql");
  }
  
  public static boolean contains(long guildId, long channelId) throws SQLException {
    return !SQL.query("textchannel/SelectTextChannel.sql", guildId, channelId).isEmpty();
  }
  
  public static int delete(long guildId, long channelId) throws SQLException {
    return SQL.update("textchannel/DeleteTextChannel.sql", guildId, channelId);
  }
  
  public static int deleteAll(long guildId) throws SQLException {
    return SQL.update("textchannel/DeleteAllTextChannel.sql", guildId);
  }
  
  public static int put(Guild guild, TextChannel channel) throws SQLException {
    return SQL.insert("textchannel/InsertTextChannel.sql", (stmt) -> {
      stmt.setLong(1, guild.getId());
      stmt.setLong(2, channel.getId());
      stmt.setString(3, channel.getName());
      // Serialize List<String> to String
      stmt.setString(4, SqlQuery.serialize(channel.getSubreddits()));
    });
  }
  
  public static TextChannel get(long guildId, long channelId) throws SQLException {
    List<SqlObject> result = SQL.query("textchannel/SelectTextChannel.sql", guildId, channelId);
    
    if (result.isEmpty()) {
      throw new NoSuchElementException();
    }
  
    SqlObject channel = transform(result.get(0));
  
    return SqlQuery.unmarshal(channel, TextChannel.class);
  }
  
  public static List<TextChannel> getAll(long guildId) throws SQLException {
    List<SqlObject> result = SQL.query("textchannel/SelectAllTextChannel.sql", guildId);
    
    return result.stream()
          .map(TextChannelTable::transform)
          .map(obj -> SqlQuery.unmarshal(obj, TextChannel.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  private static SqlObject transform(SqlObject obj) {
    // Serialize String to List<String>
    obj.computeIfPresent("subreddits", (k, v) -> SqlQuery.deserialize(v));
    return obj;
  }
}
