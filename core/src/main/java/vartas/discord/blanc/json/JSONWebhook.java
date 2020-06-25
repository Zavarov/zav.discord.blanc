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
import vartas.discord.blanc.Message;
import vartas.discord.blanc.Webhook;

public class JSONWebhook extends Webhook {
    public static final String ID = "id";
    public static final String NAME = "name";

    public static JSONWebhook of(JSONObject jsonObject){
        JSONWebhook jsonWebhook = new JSONWebhook();

        jsonWebhook.setId(jsonObject.getLong(ID));
        jsonWebhook.setName(jsonObject.getString(NAME));

        return jsonWebhook;
    }

    public static JSONObject of(Webhook webhook){
        JSONObject jsonWebhook = new JSONObject();

        jsonWebhook.put(ID, webhook.getId());
        jsonWebhook.put(NAME, webhook.getName());

        return jsonWebhook;
    }

    @Override
    public void send(Message message) {
        throw new UnsupportedOperationException("Not supported for JSON instances");
    }

    @Override
    public void send(byte[] bytes, String qualifiedName) {
        throw new UnsupportedOperationException("Not supported for JSON instances");
    }
}
