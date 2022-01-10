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

package zav.discord.blanc.api.internal.guice;

import com.google.inject.AbstractModule;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * The module containing the fields of each individual shard.
 */
public class ShardModule extends AbstractModule {
  private static final ScheduledExecutorService queue = Executors.newScheduledThreadPool(16);
  
  @Override
  protected void configure() {
    bind(ExecutorService.class).toInstance(queue);
    bind(ScheduledExecutorService.class).toInstance(queue);
  }
}
