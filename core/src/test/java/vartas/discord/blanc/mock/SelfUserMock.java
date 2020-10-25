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

import vartas.discord.blanc.SelfUser;

import java.io.InputStream;

public class SelfUserMock extends SelfUser {
    @Override
    public String getAsMention() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyStatusMessage(String statusMessage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyAvatar(InputStream avatar) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SelfUser getRealThis() {
        return this;
    }
}
