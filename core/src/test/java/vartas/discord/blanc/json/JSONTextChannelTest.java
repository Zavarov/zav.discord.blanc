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

package vartas.discord.blanc.json;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.Message;
import vartas.discord.blanc.Webhook;
import vartas.discord.blanc.mock.MessageMock;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class JSONTextChannelTest extends AbstractJSONTest{
    @Test
    public void testGetName(){
        assertThat(TEXT_CHANNEL.getName()).isEqualTo(JSON_TEXT_CHANNEL.getString(JSONTextChannel.NAME));
    }

    @Test
    public void testGetId(){
        assertThat(TEXT_CHANNEL.getId()).isEqualTo(JSON_WEBHOOK.getLong(JSONTextChannel.ID));
    }

    @Test
    public void testGetWebhooks(){
        List<Webhook> webhooks = new ArrayList<>(TEXT_CHANNEL.valuesWebhooks());

        assertEquals(webhooks.size(), 1);

        Webhook webhook = webhooks.get(0);
        assertEquals(webhook.getId(), 112233445566778899L);
        assertEquals(webhook.getName(), "redditdev");
    }

    @Test
    public void testGetSubreddits(){
        assertThat(TEXT_CHANNEL.getSubreddits()).containsExactly("redditdev");
    }

    @Test
    public void testGetJsonObject(){
        JSONObject jsonTextChannel = JSONTextChannel.of(TEXT_CHANNEL);

        assertTrue(JSON_TEXT_CHANNEL.similar(jsonTextChannel));
    }

    @Test
    public void testGetAsMention(){
        assertThrows(UnsupportedOperationException.class, () -> TEXT_CHANNEL.getAsMention());
    }

    @Test
    public void testSendMessage(){
        Message message = new MessageMock();
        assertThrows(UnsupportedOperationException.class, () -> TEXT_CHANNEL.send(message));
    }

    @Test
    public void testSendBytes(){
        byte[] bytes = new byte[0];
        String name = "";
        assertThrows(UnsupportedOperationException.class, () -> TEXT_CHANNEL.send(bytes, name));
    }
}