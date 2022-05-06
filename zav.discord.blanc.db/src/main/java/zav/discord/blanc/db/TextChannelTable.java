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

package zav.discord.blanc.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.db.sql.SqlObject;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Utility class for communicating with the {@code TextChannel} database.
 *
 * @deprecated Deprecated in favor of {@link WebhookTable}.
 */
@Singleton
@Deprecated
public class TextChannelTable extends AbstractTable<TextChannelEntity> {
  
  @Override
  protected void create() throws SQLException {
    Objects.requireNonNull(sql);
    sql.update("textchannel/CreateTextChannelTable.sql");
  }
  
  @Override
  public int put(TextChannelEntity entity) throws SQLException {
    Objects.requireNonNull(sql);
    return sql.update("textchannel/InsertTextChannel.sql", (stmt) -> {
      stmt.setLong(1, entity.getGuildId());
      stmt.setLong(2, entity.getId());
      stmt.setString(3, entity.getName());
      // Serialize List<String> to String
      stmt.setString(4, SqlQuery.marshal(entity.getSubreddits()));
    });
  }
  
  /**
   * Removes the {@link TextChannel} from the database.
   *
   * @param textChannel The {@link TextChannel} instance that is removed from the database.
   * @return The number of modified rows.
   * @throws SQLException If a database error occurred.
   */
  public int delete(TextChannel textChannel) throws SQLException {
    Objects.requireNonNull(sql);
    
    long guildId = textChannel.getGuild().getIdLong();
    long textChannelId = textChannel.getIdLong();
    
    return sql.update("textchannel/DeleteTextChannel.sql", guildId, textChannelId);
  }
  
  /**
   * Removes all text channels of the {@link Guild} from the database.
   *
   * @param guild The {@link Guild} instance whose text channels are removed from the database.
   * @return The number of modified rows.
   * @throws SQLException If a database error occurred.
   */
  public int delete(Guild guild) throws SQLException {
    Objects.requireNonNull(sql);
    
    long guildId = guild.getIdLong();
    
    return sql.update("textchannel/DeleteAllGuildTextChannel.sql", guildId);
  }
  
  /**
   * Retrieves all text channel entities of the provided {@link Guild}.
   *
   * @param guild The {@link Guild} instance whose text channels are removed from the database.
   * @return An unmodifiable list of all text channel entities associated with the provided
   *     {@link Guild}.
   * @throws SQLException If a database error occurred.
   */
  public List<TextChannelEntity> get(Guild guild) throws SQLException {
    Objects.requireNonNull(sql);
    
    long guildId = guild.getIdLong();
    
    List<SqlObject> result = sql.query("textchannel/SelectAllGuildTextChannel.sql", guildId);

    return result.stream()
          .map(TextChannelTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, TextChannelEntity.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  /**
   * Retrieves the entity associated with the provided {@link TextChannel}.
   *
   * @param textChannel The {@link TextChannel} instance that is retrieved from the database.
   * @return The entity associated with the {@link TextChannel} or {@link Optional#empty()} if no
   *     matching entity exists in the database.
   * @throws SQLException If a database error occurred.
   */
  public Optional<TextChannelEntity> get(TextChannel textChannel) throws SQLException {
    Objects.requireNonNull(sql);
    
    long guildId = textChannel.getGuild().getIdLong();
    long channelId = textChannel.getIdLong();
    
    List<SqlObject> result = sql.query("textchannel/SelectTextChannel.sql", guildId, channelId);

    return result.stream()
          .map(TextChannelTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, TextChannelEntity.class))
          .findFirst();
  }
  
  private static SqlObject transform(SqlObject obj) {
    // Serialize String to List<String>
    obj.computeIfPresent("subreddits", (k, v) -> SqlQuery.deserialize(v));
    return obj;
  }
}
