package zav.discord.blanc.db;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import zav.discord.blanc.databind.Guild;
import zav.discord.blanc.databind.TextChannel;
import zav.discord.blanc.databind.WebHook;
import zav.discord.blanc.db.internal.SqlObject;
import zav.discord.blanc.db.internal.SqlQuery;

/**
 * Utility class for communicating with the {@code WebHook} database.
 */
public abstract class WebHookTable {
  private static final SqlQuery SQL = new SqlQuery(SqlQuery.WEBHOOK_DB);
  private WebHookTable() {}
  
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
  
  public static int put(Guild guild, TextChannel channel, WebHook hook) throws SQLException {
    return SQL.insert("webhook/InsertWebHook.sql", (stmt) -> {
      stmt.setLong(1, guild.getId());
      stmt.setLong(2, channel.getId());
      stmt.setLong(3, hook.getId());
      stmt.setString(4, hook.getName());
      // Serialize List<String> to String
      stmt.setString(5, SqlQuery.serialize(hook.getSubreddits()));
    });
  }
  
  public static WebHook get(long guildId, long channelId, long hookId) throws SQLException {
    List<SqlObject> result = SQL.query("webhook/SelectWebHook.sql", guildId, channelId, hookId);
    
    if (result.isEmpty()) {
      throw new NoSuchElementException();
    }
    
    SqlObject hook = transform(result.get(0));
    
    return SqlQuery.unmarshal(hook, WebHook.class);
  }
  
  public static List<WebHook> getAll(long guildId) throws SQLException {
    List<SqlObject> result = SQL.query("webhook/SelectAllWebHook.sql", guildId);
    
    return result.stream()
          .map(WebHookTable::transform)
          .map(obj -> SqlQuery.unmarshal(obj, WebHook.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  private static SqlObject transform(SqlObject obj) {
    // Serialize String to List<String>
    obj.computeIfPresent("subreddits", (k, v) -> SqlQuery.deserialize(v));
    return obj;
  }
}
