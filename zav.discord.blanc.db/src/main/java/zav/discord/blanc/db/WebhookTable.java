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
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.db.sql.SqlObject;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Utility class for communicating with the {@code Webhook} database.
 */
@Singleton
public class WebhookTable extends AbstractTable<WebhookEntity> {
  
  @Inject
  public WebhookTable(SqlQuery sql) {
    super(sql);
  }
  
  @Override
  protected void create() throws SQLException {
    sql.update("webhook/CreateWebhookTable.sql");
  }
  
  @Override
  public int put(WebhookEntity entity) throws SQLException {
    return sql.update("webhook/InsertWebhook.sql", (stmt) -> {
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
    return sql.update("webhook/DeleteGuildWebhook.sql", guild.getIdLong());
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
    long guildId = textChannel.getGuild().getIdLong();
    long channelId = textChannel.getIdLong();
    
    return sql.update("webhook/DeleteChannelWebhook.sql", guildId, channelId);
  }
  
  /**
   * Removes the {@link Webhook} from the database.
   *
   * @param webhook The {@link Webhook} instance that is removed from the database.
   * @return The number of modified rows.
   * @throws SQLException If a database error occurred.
   */
  public int delete(Webhook webhook) throws SQLException {
    long guildId = webhook.getGuild().getIdLong();
    long channelId = webhook.getChannel().getIdLong();
    long id = webhook.getIdLong();
    
    return sql.update("webhook/DeleteWebhook.sql", guildId, channelId, id);
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
    List<SqlObject> result = sql.query("webhook/SelectGuildWebhook.sql", guild.getIdLong());
  
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
    long guildId = textChannel.getGuild().getIdLong();
    long id = textChannel.getIdLong();
    
    List<SqlObject> result = sql.query("webhook/SelectChannelWebhook.sql", guildId, id);
    
    return result.stream()
          .map(WebhookTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, WebhookEntity.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  /**
   * Retrieves the entity associated with the provided {@link Webhook}.
   *
   * @param webhook The {@link Webhook} instance that is retrieved from the database.
   * @return The entity associated with the {@link Webhook} or {@link Optional#empty()} if no
   *     matching entity exists in the database.
   * @throws SQLException If a database error occurred.
   */
  public List<WebhookEntity> get(Webhook webhook) throws SQLException {
    long guildId = webhook.getGuild().getIdLong();
    long channelId = webhook.getChannel().getIdLong();
    long id = webhook.getIdLong();
    
    List<SqlObject> result = sql.query("webhook/SelectWebhook.sql", guildId, channelId, id);
    
    return result.stream()
          .map(WebhookTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, WebhookEntity.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  private static SqlObject transform(SqlObject obj) {
    // Serialize String to List<String>
    obj.computeIfPresent("subreddits", (k, v) -> SqlQuery.deserialize(v));
    return obj;
  }
}
