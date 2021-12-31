package zav.discord.blanc.db;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.db.internal.SqlObject;
import zav.discord.blanc.db.internal.SqlQuery;

/**
 * Utility class for communicating with the {@code Guild} database.
 */
public abstract class GuildDatabase {
  private static final SqlQuery SQL = new SqlQuery(SqlQuery.GUILD_DB);
  
  private GuildDatabase() {}
  
  public static void create() throws SQLException {
    SQL.update("guild/CreateGuildTable.sql");
  }
  
  public static boolean contains(long guildId) throws SQLException {
    return !SQL.query("guild/SelectGuild.sql", guildId).isEmpty();
  }
  
  public static int delete(long guildId) throws SQLException {
    return SQL.update("guild/DeleteGuild.sql", guildId);
    
  }
  
  /**
   * Serializes the guild and stores its value in the database.<br>
   * Guilds are identified by their id.<br>
   * If the database doesn't contain an entry for the guild, a new one is created. Otherwise the
   * old entry is overwritten.
   *
   * @param guild The {@code guild} instance stored in the database.
   * @return The number of lines modified. Should be {@code 1}.
   * @throws SQLException If a database error occurred.
   */
  public static int put(GuildValueObject guild) throws SQLException {
    return SQL.update("guild/InsertGuild.sql", (stmt) -> {
      stmt.setLong(1, guild.getId());
      stmt.setString(2, guild.getName());
      stmt.setString(3, guild.getPrefix().orElse(null));
      // Serialize List<String> to String
      stmt.setString(4, SqlQuery.marshal(guild.getBlacklist()));
    });
  }
  
  /**
   * Retrieves the database entry corresponding to the requested {@code guild} and deserializes its
   * content.
   * Guilds are identified by their id.<br>
   * If no such entry exists, an {@link NoSuchElementException} is thrown.
   *
   * @param guildId The id of the requested {@code guild}.
   * @return The deserialized {@code guild} instance retrieved from the database.
   * @throws SQLException If a database error occurred.
   * @throws NoSuchElementException if the database doesn't contain an entry with the specified id.
   */
  public static GuildValueObject get(long guildId) throws SQLException {
    List<SqlObject> result = SQL.query("guild/SelectGuild.sql", guildId);
      
    if (result.isEmpty()) {
      throw new NoSuchElementException();
    }
    
    SqlObject guild = result.get(0);
  
    // Serialize String to List<String>
    guild.computeIfPresent("blacklist", (k, v) -> SqlQuery.deserialize(v));
      
    return SqlQuery.unmarshal(guild, GuildValueObject.class);
  }
}
