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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import net.dv8tion.jda.api.JDA;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.util.ApplicationContext;
import zav.discord.blanc.api.util.ShardSupplier;

/**
 * The application instance over all shards.
 */
@NonNullByDefault
public class Client implements ApplicationContext {
  private final List<JDA> shards = new ArrayList<>();
  private final Map<Class<?>, Object> context = new HashMap<>();
  
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
  @Contract(pure = true)
  public JDA getShard(long guildId) {
    // @See https://discord.com/developers/docs/topics/gateway#sharding
    long index = (guildId >> 22) % shards.size();
    return shards.get((int) index);
  }

  @Override
  @Contract(pure = true)
  public <T> T get(Class<T> clazz) {
    Object result = context.get(clazz);

    if (result == null) {
      throw new NoSuchElementException("No element bound for " + clazz.toString());
    }

    return clazz.cast(result);
  }

  @Override
  @Contract(mutates = "this")
  public <T> void bind(Class<T> clazz, T object) {
    context.put(clazz, object);
  }
}
