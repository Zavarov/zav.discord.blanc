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

import vartas.discord.blanc.Member;
import vartas.discord.blanc.Permission;
import vartas.discord.blanc.Role;
import vartas.discord.blanc.TextChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class MemberMock extends Member {
    public List<Permission> permissions = new ArrayList<>();

    @Nonnull
    @Override
    public List<Permission> getPermissions(@Nonnull TextChannel textChannel) {
        return  permissions;
    }

    @Override
    public Optional<String> retrieveNickname() {
        return Optional.empty();
    }

    @Override
    public List<Role> retrieveRoles() {
        return Collections.emptyList();
    }

    @Override
    public void modifyRoles(Collection<Role> rolesToAdd, Collection<Role> rolesToRemove) {
        throw new UnsupportedOperationException();
    }
}