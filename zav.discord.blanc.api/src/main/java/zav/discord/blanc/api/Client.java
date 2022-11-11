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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.util.AbstractApplicationContext;
import zav.discord.blanc.api.util.ShardSupplier;

/**
 * The application instance over all shards.
 */
@NonNullByDefault
public class Client extends AbstractApplicationContext {
  private final List<Shard> shards = new ArrayList<>();
  
  /**
   * Creates and initializes all shards.
   *
   * @param supplier The provider used to create those instances.
   */
  @Contract(mutates = "this")
  public void postConstruct(ShardSupplier supplier) {
    supplier.forEachRemaining(shards::add);
  }
  
  /**
   * Returns an immutable list of all shard instance.
   *
   * @return As described.
   */
  @Contract(pure = true)
  public List<Shard> getShards() {
    return List.copyOf(shards);
  }
  
  /**
   * The shard in which a guild is located is determined using the following formula:
   * {@code (guild id >> 22) / #shards}.
   *
   * @param guildId A guild id.
   * @return The shard in which the guild is located.
   */
  @Contract(pure = true)
  public Shard getShard(long guildId) {
    // @See https://discord.com/developers/docs/topics/gateway#sharding
    long index = (guildId >> 22) % shards.size();
    return shards.get((int) index);
  }
}
