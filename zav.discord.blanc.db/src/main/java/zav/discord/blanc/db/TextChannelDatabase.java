package zav.discord.blanc.db;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import zav.discord.blanc.databind.GuildDto;
import zav.discord.blanc.databind.TextChannelDto;
import zav.discord.blanc.db.internal.SqlObject;
import zav.discord.blanc.db.internal.SqlQuery;

/**
 * Utility class for communicating with the {@code TextChannel} database.
 */
public abstract class TextChannelDatabase {
  private static final SqlQuery SQL = new SqlQuery(SqlQuery.TEXTCHANNEL_DB);
  
  private TextChannelDatabase() {}
  
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
  
  /**
   * Serializes the channel and stores its value in the database.<br>
   * Text Channels are identified by their id and the id of their associated {@code guild}.<br>
   * If the database doesn't contain an entry for the channel, a new one is created. Otherwise the
   * old entry is overwritten.
   *
   * @param guild The {@code guild} instance associated with the role.
   * @param channel The {@code text channel} instance stored in the database.
   * @return The number of lines modified. Should be {@code 1}.
   * @throws SQLException If a database error occurred.
   */
  public static int put(GuildDto guild, TextChannelDto channel) throws SQLException {
    return SQL.update("textchannel/InsertTextChannel.sql", (stmt) -> {
      stmt.setLong(1, guild.getId());
      stmt.setLong(2, channel.getId());
      stmt.setString(3, channel.getName());
      // Serialize List<String> to String
      stmt.setString(4, SqlQuery.marshal(channel.getSubreddits()));
    });
  }
  
  /**
   * Retrieves the database entry corresponding to the requested {@code text channel} and
   * deserializes its content.
   * Text channels are identified by their id and guild id.<br>
   * If no such entry exists, an {@link NoSuchElementException} is thrown.
   *
   * @param guildId The guild id of the {@code channel}.
   * @param channelId The id of the requested {@code channel}.
   * @return The deserialized {@code channel} instance retrieved from the database.
   * @throws SQLException If a database error occurred.
   * @throws NoSuchElementException if the database doesn't contain an entry with the specified id.
   */
  public static TextChannelDto get(long guildId, long channelId) throws SQLException {
    List<SqlObject> result = SQL.query("textchannel/SelectTextChannel.sql", guildId, channelId);
    
    if (result.isEmpty()) {
      throw new NoSuchElementException();
    }
  
    SqlObject channel = transform(result.get(0));
  
    return SqlQuery.unmarshal(channel, TextChannelDto.class);
  }
  
  /**
   * Retrieves the database entries corresponding to the requested {@code guild}.
   *
   * @param guildId The id of the {@code guild} associated with the requested {@code channels}.
   * @return An unmodifiable list of all text channels associated with the provided guild id.
   * @throws SQLException If a database error occurred.
   */
  public static List<TextChannelDto> getAll(long guildId) throws SQLException {
    List<SqlObject> result = SQL.query("textchannel/SelectAllTextChannel.sql", guildId);
    
    return result.stream()
          .map(TextChannelDatabase::transform)
          .map(obj -> SqlQuery.unmarshal(obj, TextChannelDto.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  private static SqlObject transform(SqlObject obj) {
    // Serialize String to List<String>
    obj.computeIfPresent("subreddits", (k, v) -> SqlQuery.deserialize(v));
    return obj;
  }
}
