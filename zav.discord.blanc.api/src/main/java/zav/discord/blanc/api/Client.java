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

package zav.discord.blanc.api;

import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import zav.discord.blanc.api.internal.JdaShardSupplier;

/**
 * The application instance over all shards.
 */
@Singleton
public class Client {
  private final List<JDA> shards = new ArrayList<>();
  
  @Inject
  private void postConstruct(Injector injector) {
    JdaShardSupplier supplier = injector.getInstance(JdaShardSupplier.class);
    supplier.forEachRemaining(shards::add);
  }
  
  public List<JDA> getShards() {
    return List.copyOf(shards);
  }
  
  /**
   * The shard in which a guild is located is determined using the following formula:
   * {@code (guild id >> 22) / #shards}.
   *
   * @param guildId A guild id.
   * @return The shard in which the guild is located.
   */
  public JDA getShard(long guildId) {
    // @See https://discord.com/developers/docs/topics/gateway#sharding
    long index = (guildId >> 22) % shards.size();
    return shards.get((int) index);
  }
}
