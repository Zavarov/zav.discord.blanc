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
import net.dv8tion.jda.api.entities.Webhook;
import org.apache.commons.lang3.StringUtils;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.db.sql.SqlObject;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Utility class for communicating with the {@code Webhook} database.
 */
@Singleton
public class WebhookTable extends AbstractTable<WebhookEntity, Webhook> {
  
  @Inject
  public WebhookTable(SqlQuery sql) {
    super(sql);
  }
  
  @Override
  protected void create() throws SQLException {
    sql.update("db/webhook/Create.sql");
  }
  
  @Override
  public int put(WebhookEntity entity) throws SQLException {
    return sql.update("db/webhook/Insert.sql", (stmt) -> {
      stmt.setLong(1, entity.getGuildId());
      stmt.setLong(2, entity.getChannelId());
      stmt.setLong(3, entity.getId());
      stmt.setString(4, entity.getName());
      // Serialize List<String> to String
      stmt.setString(5, SqlQuery.marshal(entity.getSubreddits()));
      stmt.setBoolean(6, entity.isOwner());
    });
  }
  
  public int delete(Guild guild) throws SQLException {
    return sql.update("db/webhook/DeleteGuild.sql", guild.getId());
  }
  
  /**
   * Removes all webhooks of the {@link TextChannel} from the database.
   *
   * @param textChannel The {@link TextChannel} instance whose text channels are removed from the
   *     database.
   * @return The number of modified rows.
   * @throws SQLException If a database error occurred.
   */
  public int delete(TextChannel textChannel) throws SQLException {
    String guildId = textChannel.getGuild().getId();
    String channelId = textChannel.getId();
    
    return sql.update("db/webhook/DeleteChannel.sql", guildId, channelId);
  }
  
  @Override
  public int delete(Webhook webhook) throws SQLException {
    String guildId = webhook.getGuild().getId();
    String channelId = webhook.getChannel().getId();
    String id = webhook.getId();
    
    return sql.update("db/webhook/Delete.sql", guildId, channelId, id);
  }
  
  public int retain(Guild guild) throws SQLException {
    int result = 0;
    
    for (TextChannel textChannel : guild.getTextChannels()) {
      result += retain(guild, textChannel);
    }
    
    return result;
  }
  
  private int retain(Guild guild, TextChannel textChannel) throws SQLException {
    String guildId = guild.getId();
    String channelId = textChannel.getId();
    String ids = transform(textChannel.retrieveWebhooks().complete());
    return sql.update("db/webhook/Retain.sql", guildId, channelId, ids);
  }
  
  /**
   * Retrieves all Webhook entities of the provided {@link Guild}.
   *
   * @param guild The {@link Guild} instance whose text channels are removed from the database.
   * @return An unmodifiable list of all webhook entities associated with the provided
   *     {@link Guild}.
   * @throws SQLException If a database error occurred.
   */
  public List<WebhookEntity> get(Guild guild) throws SQLException {
    List<SqlObject> result = sql.query("db/webhook/SelectGuild.sql", guild.getId());
  
    return result.stream()
          .map(WebhookTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, WebhookEntity.class))
        .collect(Collectors.toUnmodifiableList());
  }
  
  /**
   * Retrieves all Webhook entities of the provided {@link TextChannel}.
   *
   * @param textChannel The {@link TextChannel} instance whose text channels are removed from the
   *     database.
   * @return An unmodifiable list of all webhook entities associated with the provided
   *     {@link TextChannel}.
   * @throws SQLException If a database error occurred.
   */
  public List<WebhookEntity> get(TextChannel textChannel) throws SQLException {
    String guildId = textChannel.getGuild().getId();
    String id = textChannel.getId();
    
    List<SqlObject> result = sql.query("db/webhook/SelectChannel.sql", guildId, id);
    
    return result.stream()
          .map(WebhookTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, WebhookEntity.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  @Override
  public Optional<WebhookEntity> get(Webhook webhook) throws SQLException {
    String guildId = webhook.getGuild().getId();
    String channelId = webhook.getChannel().getId();
    String id = webhook.getId();
    
    List<SqlObject> result = sql.query("db/webhook/Select.sql", guildId, channelId, id);
    
    return result.stream()
          .map(WebhookTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, WebhookEntity.class))
          .findFirst();
  }
  
  private static String transform(Collection<? extends ISnowflake> source) {
    return source.stream()
          .map(ISnowflake::getId)
          .reduce((u, v) -> u + "," + v)
          .orElse(StringUtils.EMPTY);
  }
  
  private static SqlObject transform(SqlObject obj) {
    // Serialize String to List<String>
    obj.computeIfPresent("subreddits", (k, v) -> SqlQuery.deserialize(v));
    return obj;
  }
}
