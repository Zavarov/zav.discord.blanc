package zav.discord.blanc.db;

import zav.discord.blanc.databind.Guild;
import zav.discord.blanc.db.internal.SqlObject;
import zav.discord.blanc.db.internal.SqlQuery;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class GuildTable {
  private static final SqlQuery SQL = new SqlQuery(SqlQuery.GUILD_DB);
  private GuildTable() {}
  
  public static void create() throws SQLException {
    SQL.update("guild/CreateGuildTable.sql");
  }
  
  public static boolean contains(long guildId) throws SQLException {
    return !SQL.query("guild/SelectGuild.sql", guildId).isEmpty();
  }
  
  public static int delete(long guildId) throws SQLException {
    return SQL.update("guild/DeleteGuild.sql", guildId);
    
  }
  
  public static int put(Guild guild) throws SQLException {
    return SQL.insert("guild/InsertGuild.sql", (stmt) -> {
      stmt.setLong(1, guild.getId());
      stmt.setString(2, guild.getName());
      stmt.setString(3, guild.getPrefix());
      // Serialize List<String> to String
      stmt.setString(4, SqlQuery.serialize(guild.getBlacklist()));
    });
  }
  
  public static Guild get(long guildId) throws SQLException {
    List<SqlObject> result = SQL.query("guild/SelectGuild.sql", guildId);
      
    if (result.isEmpty()) {
      throw new NoSuchElementException();
    }
    
    SqlObject guild = result.get(0);
  
    // Serialize String to List<String>
    guild.computeIfPresent("blacklist", (k,v) -> SqlQuery.deserialize(v));
      
    return SqlQuery.unmarshal(guild, Guild.class);
  }
}
