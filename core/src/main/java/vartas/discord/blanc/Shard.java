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

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.io.json.JSONCredentials;
import vartas.discord.blanc.json.JSONGuild;
import vartas.discord.blanc.visitor.RedditVisitor;

import javax.annotation.Nonnull;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Shard extends ShardTOP{
    @Nonnull
    protected final ScheduledExecutorService executor;

    public Shard(){
        this.executor = Executors.newScheduledThreadPool(1);
    }

    public Shard(@Nonnull RedditVisitor redditVisitor){
        this();
        //Request submissions every minute with one minute initial delay
        this.executor.scheduleAtFixedRate(() -> this.accept(redditVisitor), 1, 1, TimeUnit.MINUTES);
    }

    public Shard(@Nonnull RedditVisitor redditVisitor, @Nonnull StatusMessageRunnable statusMessageRunnable){
        this(redditVisitor);
        this.executor.scheduleAtFixedRate(statusMessageRunnable, 0, JSONCredentials.CREDENTIALS.getStatusMessageUpdateInterval(), TimeUnit.MINUTES);
    }

    public static synchronized void write(JSONObject jsonObject, Path target){
        try{
            FileWriter writer = new FileWriter(target.toFile());
            jsonObject.write(writer, 4, 0);
            writer.close();
        }catch(IOException e){
            LoggerFactory.getLogger(Shard.class.getSimpleName()).error(e.toString());
        }
    }

    public static void write(Guild guild){
        JSONObject jsonGuild = JSONGuild.of(guild);
        write(jsonGuild, JSONCredentials.CREDENTIALS.getGuildDirectory().resolve(guild.getId()+".gld"));
    }
}
