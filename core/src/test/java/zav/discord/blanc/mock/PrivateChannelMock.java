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

import zav.discord.blanc._factory.PrivateChannelFactory;
import zav.discord.blanc.Message;
import zav.discord.blanc.PrivateChannel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PrivateChannelMock extends PrivateChannel {
    public Map<Long, Message> messages = new HashMap<>();

    public PrivateChannelMock(){}
    public PrivateChannelMock(int id, String name){
        PrivateChannelFactory.create(() -> this, id, name);
    }

    @Override
    public void send(Message message) {
        messages.put(message.getId(), message);
    }

    @Override
    public void send(byte[] bytes, String qualifiedName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Message> retrieveMessage(long id) {
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public Collection<Message> retrieveMessages() {
        return messages.values();
    }
}
