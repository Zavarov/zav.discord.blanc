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

import org.jetbrains.annotations.NotNull;
import zav.discord.blanc._factory.WebhookFactory;
import zav.discord.blanc.Message;
import zav.discord.blanc.Webhook;
import zav.jra.models.AbstractLink;
import zav.jra.models.AbstractSubreddit;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WebhookMock extends Webhook {
    public Map<Long, Message> messages = new HashMap<>();

    public IOException sendLinkException = null;

    public WebhookMock(){}

    public WebhookMock(long id, String name){
        WebhookFactory.create(() -> this, id, name);
    }

    @Override
    public Optional<Message> retrieveMessage(long id) {
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public Collection<Message> retrieveMessages() {
        return messages.values();
    }

    @Override
    public void send(@NotNull AbstractSubreddit subreddit, @NotNull AbstractLink link) throws IOException {
        if(sendLinkException != null){
            throw sendLinkException;
        }
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
