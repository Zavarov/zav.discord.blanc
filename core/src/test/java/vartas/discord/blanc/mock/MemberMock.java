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
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import vartas.discord.blanc.$factory.MemberFactory;
import vartas.discord.blanc.*;

import javax.annotation.Nonnull;
import java.util.*;

public class MemberMock extends Member {
    public Map<Long, Role> roles = new HashMap<>();
    public SetMultimap<TextChannel, Permission> permissions = HashMultimap.create();
    public PrivateChannel privateChannel;

    public MemberMock(){}
    public MemberMock(int id, String name){
        MemberFactory.create(() -> this, OnlineStatus.ONLINE, id, name);
    }


    @Nonnull
    @Override
    public Set<Permission> getPermissions(@Nonnull TextChannel textChannel) {
        return permissions.get(textChannel);
    }

    @Override
    public Optional<String> retrieveNickname() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Role> retrieveRoles() {
        return roles.values();
    }

    @Override
    public void modifyRoles(Collection<Role> rolesToAdd, Collection<Role> rolesToRemove) {
        for(Role role : rolesToAdd)
            roles.put(role.getId(), role);
        for(Role role : rolesToRemove)
            roles.remove(role.getId());
    }

    @Override
    public PrivateChannel retrievePrivateChannel() {
        return Preconditions.checkNotNull(privateChannel);
    }

    @Override
    public String getAsMention() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public MessageEmbed toMessageEmbed() {
        throw new UnsupportedOperationException();
    }
}
