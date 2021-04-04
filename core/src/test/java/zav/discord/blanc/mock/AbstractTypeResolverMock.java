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

package zav.discord.blanc.mock;

import zav.discord.blanc.*;
import zav.discord.blanc.Guild;
import zav.discord.blanc.parser.AbstractTypeResolver;
import zav.discord.blanc.parser.Argument;

import java.util.NoSuchElementException;

public class AbstractTypeResolverMock extends AbstractTypeResolver {
    @Override
    public Guild resolveGuild(Argument argument) throws NoSuchElementException {
        throw new UnsupportedOperationException();
    }

    @Override
    public User resolveUser(Argument argument) throws NoSuchElementException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Member resolveMember(Argument argument) throws NoSuchElementException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message resolveMessage(Argument argument) throws NoSuchElementException {
        throw new UnsupportedOperationException();
    }

    @Override
    public TextChannel resolveTextChannel(Argument argument) throws NoSuchElementException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Role resolveRole(Argument argument) throws NoSuchElementException {
        throw new UnsupportedOperationException();
    }
}
