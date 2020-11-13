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

import com.google.common.base.Preconditions;
import vartas.discord.blanc.$factory.ShardFactory;
import vartas.discord.blanc.Guild;
import vartas.discord.blanc.SelfUser;
import vartas.discord.blanc.Shard;
import vartas.discord.blanc.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ShardMock extends Shard {
    public Map<Long, User> users = new HashMap<>();
    public Map<Long, Guild> guilds = new HashMap<>();
    public SelfUser selfUser;

    public ShardMock(){}

    public ShardMock(int id){
        ShardFactory.create(() -> this, id);
    }

    @Override
    public SelfUser retrieveSelfUser() {
        return Preconditions.checkNotNull(selfUser);
    }

    @Override
    public Optional<User> retrieveUser(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> retrieveUsers() {
        return users.values();
    }

    @Override
    public Optional<Guild> retrieveGuild(long id) {
        return Optional.ofNullable(guilds.get(id));
    }

    @Override
    public Collection<Guild> retrieveGuilds() {
        return guilds.values();
    }
}
