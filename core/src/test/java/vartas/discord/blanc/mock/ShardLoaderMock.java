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

package vartas.discord.blanc.mock;

import org.json.JSONException;
import vartas.discord.blanc.$factory.ShardFactory;
import vartas.discord.blanc.$json.JSONGuild;
import vartas.discord.blanc.Guild;
import vartas.discord.blanc.Shard;
import vartas.discord.blanc.ShardLoader;
import vartas.discord.blanc.io.Credentials;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class ShardLoaderMock extends ShardLoader {
    public ShardLoaderMock(Credentials credentials) {
        super(credentials);
    }

    @Override
    public Shard load(int shardId) {
        Shard shard = ShardFactory.create(shardId, new SelfUserMock());
        shard.accept(this);
        return shard;
    }

    @Nonnull
    public Optional<Guild> load(@Nonnull Path guildPath){
        try{
            Guild jsonGuild = JSONGuild.fromJson(new GuildMock(), guildPath);
            jsonGuild.setSelfMember(new SelfMemberMock());
            return Optional.of(jsonGuild);
        }catch(IOException | JSONException e){
            return Optional.empty();
        }
    }
}
