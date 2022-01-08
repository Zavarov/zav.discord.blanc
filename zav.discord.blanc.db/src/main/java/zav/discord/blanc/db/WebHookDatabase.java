package zav.discord.blanc.db;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import zav.discord.blanc.databind.GuildDto;
import zav.discord.blanc.databind.TextChannelDto;
import zav.discord.blanc.databind.WebHookDto;
import zav.discord.blanc.db.internal.SqlObject;
import zav.discord.blanc.db.internal.SqlQuery;

/**
 * Utility class for communicating with the {@code WebHook} database.
 */
public abstract class WebHookDatabase {
  private static final SqlQuery SQL = new SqlQuery(SqlQuery.WEBHOOK_DB);
  
  private WebHookDatabase() {}
  
  public static void create() throws SQLException {
    SQL.update("webhook/CreateWebHookTable.sql");
  }
  
  public static boolean contains(long guildId, long channelId, long hookId) throws SQLException {
    return !SQL.query("webhook/SelectWebHook.sql", guildId, channelId, hookId).isEmpty();
  }
  
  public static int delete(long guildId, long channelId, long hookId) throws SQLException {
    return SQL.update("webhook/DeleteWebHook.sql", guildId, channelId, hookId);
  }
  
  public static int deleteAll(long guildId) throws SQLException {
    return SQL.update("webhook/DeleteAllWebHook.sql", guildId);
  }
  
  /**
   * Serializes the web hook and stores its value in the database.<br>
   * Text Channels are identified by their id and the id of their associated {@code guild} and
   * the id of the {@code channel} they are in.<br>
   * If the database doesn't contain an entry for the web hook, a new one is created. Otherwise the
   * old entry is overwritten.
   *
   * @param guild The {@code guild} instance associated with the role.
   * @param channel The {@code text channel} instance stored in the database.
   * @param hook The {@code web hook} instance stored in the database.
   * @return The number of lines modified. Should be {@code 1}.
   * @throws SQLException If a database error occurred.
   */
  public static int put(GuildDto guild, TextChannelDto channel, WebHookDto hook) throws SQLException {
    return SQL.update("webhook/InsertWebHook.sql", (stmt) -> {
      stmt.setLong(1, guild.getId());
      stmt.setLong(2, channel.getId());
      stmt.setLong(3, hook.getId());
      stmt.setString(4, hook.getName());
      // Serialize List<String> to String
      stmt.setString(5, SqlQuery.marshal(hook.getSubreddits()));
      stmt.setBoolean(6, hook.isOwner());
    });
  }
  
  /**
   * Retrieves the database entry corresponding to the requested {@code web hook} and
   * deserializes its content.
   * Text channels are identified by their id, the channel and guild id.<br>
   * If no such entry exists, an {@link NoSuchElementException} is thrown.
   *
   * @param guildId The guild id of the {@code hook}.
   * @param channelId The channel id of the {@code hook}.
   * @param hookId The id of the requested {@code hook}.
   * @return The deserialized {@code hook} instance retrieved from the database.
   * @throws SQLException If a database error occurred.
   * @throws NoSuchElementException if the database doesn't contain an entry with the specified id.
   */
  public static WebHookDto get(long guildId, long channelId, long hookId) throws SQLException {
    List<SqlObject> result = SQL.query("webhook/SelectWebHook.sql", guildId, channelId, hookId);
    
    if (result.isEmpty()) {
      throw new NoSuchElementException();
    }
    
    SqlObject hook = transform(result.get(0));
    
    return SqlQuery.unmarshal(hook, WebHookDto.class);
  }
  
  /**
   * Retrieves the database entries corresponding to the requested {@code guild}.
   *
   * @param guildId The id of the {@code guild} associated with the requested {@code web hooks}.
   * @return An unmodifiable list of all web hooks associated with the provided guild id.
   * @throws SQLException If a database error occurred.
   */
  public static List<WebHookDto> getAll(long guildId) throws SQLException {
    List<SqlObject> result = SQL.query("webhook/SelectAllWebHook.sql", guildId);
    
    return result.stream()
          .map(WebHookDatabase::transform)
          .map(obj -> SqlQuery.unmarshal(obj, WebHookDto.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  private static SqlObject transform(SqlObject obj) {
    // Serialize String to List<String>
    obj.computeIfPresent("subreddits", (k, v) -> SqlQuery.deserialize(v));
    return obj;
  }
}
