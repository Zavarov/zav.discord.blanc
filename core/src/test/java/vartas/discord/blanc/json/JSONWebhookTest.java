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
import vartas.discord.blanc.mock.MessageMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class JSONWebhookTest extends AbstractJSONTest{
    @Test
    public void testGetName(){
        assertThat(WEBHOOK.getName()).isEqualTo(JSON_WEBHOOK.getString(JSONWebhook.NAME));
    }

    @Test
    public void testGetId(){
        assertThat(WEBHOOK.getId()).isEqualTo(JSON_WEBHOOK.getLong(JSONWebhook.ID));
    }

    @Test
    public void testGetJsonObject(){
        JSONObject jsonWebhook = JSONWebhook.of(WEBHOOK);

        assertTrue(JSON_WEBHOOK.similar(jsonWebhook));
    }

    @Test
    public void testSendMessage(){
        Message message = new MessageMock();
        assertThrows(UnsupportedOperationException.class, () -> WEBHOOK.send(message));
    }

    @Test
    public void testSendBytes(){
        byte[] bytes = new byte[0];
        String name = "";
        assertThrows(UnsupportedOperationException.class, () -> WEBHOOK.send(bytes, name));
    }
}
