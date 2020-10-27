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
import vartas.discord.blanc.Webhook;

import java.util.Optional;

public class JSONWebhook extends JSONWebhookTOP {
    @Override
    protected void $fromMessages(JSONObject source, Webhook target){
        //Omitted
    }

    @Override
    protected void $toMessages(Webhook source, JSONObject target){
        //Omitted
    }

    @Override
    protected void $fromSubreddit(JSONObject source, Webhook target){
        Optional<String> subreddit = Optional.ofNullable(source.optString("subreddit"));
        target.setSubreddit(subreddit);
    }

    @Override
    protected void $toSubreddit(Webhook source, JSONObject target){
        source.ifPresentSubreddit(subreddit ->  target.put("subreddit", subreddit));
    }
}
