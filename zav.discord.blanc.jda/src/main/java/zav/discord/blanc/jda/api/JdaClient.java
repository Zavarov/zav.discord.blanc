/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.jda.api;

import com.google.inject.Injector;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.jda.JdaShardSupplier;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class JdaClient implements Client {
  private final List<Shard> shards = new ArrayList<>();
  
  @Inject
  private void postConstruct(Injector injector) {
    JdaShardSupplier supplier = injector.getInstance(JdaShardSupplier.class);
    supplier.forEachRemaining(shards::add);
  }
  
  @Override
  public List<Shard> getShards() {
    return List.copyOf(shards);
  }
  
  @Override
  public Shard getShard(long guildId) {
    // @See https://discord.com/developers/docs/topics/gateway#sharding
    long index = (guildId >> 22) % shards.size();
    return shards.get((int) index);
  }
}
