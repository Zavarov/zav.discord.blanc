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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
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
public class TextChannelTable extends AbstractTable<TextChannelEntity, TextChannel> {
  
  @Inject
  public TextChannelTable(SqlQuery sql) {
    super(sql);
  }
  
  @Override
  protected void create() throws SQLException {
    sql.update("db/textchannel/Create.sql");
  }
  
  @Override
  public int put(TextChannelEntity entity) throws SQLException {
    return sql.update("db/textchannel/Insert.sql", (stmt) -> {
      stmt.setLong(1, entity.getGuildId());
      stmt.setLong(2, entity.getId());
      stmt.setString(3, entity.getName());
      // Serialize List<String> to String
      stmt.setString(4, SqlQuery.marshal(entity.getSubreddits()));
    });
  }
  
  @Override
  public int delete(TextChannel textChannel) throws SQLException {
    String guildId = textChannel.getGuild().getId();
    String textChannelId = textChannel.getId();
    
    return sql.update("db/textchannel/Delete.sql", guildId, textChannelId);
  }
  
  /**
   * Removes all text channels of the {@link Guild} from the database.
   *
   * @param guild The {@link Guild} instance whose text channels are removed from the database.
   * @return The number of modified rows.
   * @throws SQLException If a database error occurred.
   */
  public int delete(Guild guild) throws SQLException {
    String guildId = guild.getId();
    
    return sql.update("db/textchannel/DeleteGuild.sql", guildId);
  }
  
  public int retain(Guild guild) throws SQLException {
    String guildId = guild.getId();
    String ids = transform(guild.getTextChannels());
    return sql.update("db/textchannel/Retain.sql", guildId, ids);
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
    String guildId = guild.getId();
    
    List<SqlObject> result = sql.query("db/textchannel/SelectGuild.sql", guildId);

    return result.stream()
          .map(TextChannelTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, TextChannelEntity.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  @Override
  public Optional<TextChannelEntity> get(TextChannel textChannel) throws SQLException {
    String guildId = textChannel.getGuild().getId();
    String channelId = textChannel.getId();
    
    List<SqlObject> result = sql.query("db/textchannel/Select.sql", guildId, channelId);

    return result.stream()
          .map(TextChannelTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, TextChannelEntity.class))
          .findFirst();
  }
  
  private static String transform(Collection<TextChannel> textChannels) {
    return textChannels.stream()
          .map(TextChannel::getId)
          .reduce((u, v) -> u + "," + v)
          .orElseThrow();
  }
  
  private static SqlObject transform(SqlObject obj) {
    // Serialize String to List<String>
    obj.computeIfPresent("subreddits", (k, v) -> SqlQuery.deserialize(v));
    return obj;
  }
}
