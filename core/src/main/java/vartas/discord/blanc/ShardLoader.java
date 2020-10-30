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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.$visitor.ArchitectureVisitor;
import vartas.discord.blanc.io.Credentials;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public abstract class ShardLoader implements ArchitectureVisitor {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Credentials credentials;
    private final int shardCount;

    public ShardLoader(Credentials credentials){
        this.credentials = credentials;
        this.shardCount = credentials.getShardCount();
    }

    public abstract Shard load(int shardId);

    public abstract Optional<Guild> load(@Nonnull Path guildPath);

    @Override
    public void visit(@Nonnull Shard shard){
        Path guildDirectory = credentials.getGuildDirectory();

        try {
            if(Files.notExists(guildDirectory))
                Files.createDirectories(guildDirectory);

            Files.list(guildDirectory)
                    .map(this::load)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(guild -> isInShard(shard, guild))
                    .forEach(guild -> shard.putGuilds(guild.getId(), guild));

        }catch(IOException e){
            log.error(Errors.INVALID_FILE.toString(), e);
        }
    }

    private boolean isInShard(@Nonnull ShardTOP shard, @Nonnull Guild guild){
        return (guild.getId() >> 22) % shardCount == shard.getId();
    }
}
