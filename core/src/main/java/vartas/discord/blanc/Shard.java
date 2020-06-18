/*
 * Copyright (c) 2020 Zavarov
 *
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

package vartas.discord.blanc;

import vartas.discord.blanc.visitor.RedditVisitor;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Shard extends ShardTOP{
    @Nonnull
    private final ScheduledExecutorService redditExecutor;

    public Shard(){
        this.redditExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    public Shard(@Nonnull RedditVisitor redditVisitor){
        this();
        //Request submissions every minute with one minute initial delay
        this.redditExecutor.scheduleAtFixedRate(() -> accept(redditVisitor), 1, 1, TimeUnit.MINUTES);
    }
}
