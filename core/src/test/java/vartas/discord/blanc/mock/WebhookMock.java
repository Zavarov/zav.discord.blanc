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

import vartas.discord.blanc.$factory.WebhookFactory;
import vartas.discord.blanc.Message;
import vartas.discord.blanc.Webhook;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class WebhookMock extends Webhook {
    public Map<Long, Message> messages = new HashMap<>();

    public WebhookMock(){}

    public WebhookMock(long id, String name){
        WebhookFactory.create(() -> this, id, name);
    }

    @Override
    public Message retrieveMessage(long id) {
        if(!messages.containsKey(id))
            throw new NoSuchElementException();
        return messages.get(id);
    }

    @Override
    public Collection<Message> retrieveMessages() {
        return messages.values();
    }

    @Override
    public void send(Message message) {
        messages.put(message.getId(), message);
    }

    @Override
    public void send(byte[] bytes, String qualifiedName) {
        throw new UnsupportedOperationException();
    }
}
