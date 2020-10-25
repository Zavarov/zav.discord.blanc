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

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.Errors;
import vartas.discord.blanc.TextChannel;
import vartas.discord.blanc.Webhook;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class JSONTextChannel extends JSONTextChannelTOP {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    public static final String SUBREDDITS = "subreddits";
    public static final String WEBHOOKS = "webhooks";

    @Override
    protected void $fromMessages(JSONObject source, TextChannel target){
        //Omitted
    }

    @Override
    protected void $toMessages(TextChannel source, JSONObject target){
        //Omitted
    }

    @Override
    protected void $fromWebhooks(JSONObject source, TextChannel target) {
        JSONObject webhooks = source.optJSONObject(WEBHOOKS);
        if(webhooks != null) {
            for (String key : webhooks.keySet()) {
                try {
                    Webhook webhook = target.getWebhooks(key);
                    target.putWebhooks(key, JSONWebhook.fromJson(webhook, webhooks.getJSONObject(key)));
                } catch (ExecutionException e) {
                    //The requested webhook may no longer exist.
                    log.warn(Errors.UNKNOWN_WEBHOOK.toString(), e);
                }
            }
        }
    }

    @Override
    protected void $toWebhooks(TextChannel source, JSONObject target){
        JSONObject webhooks = new JSONObject();
        for(Map.Entry<String, Webhook> webhook : source.asMapWebhooks().entrySet())
            webhooks.put(webhook.getKey(), JSONWebhook.toJson(webhook.getValue(), new JSONObject()));
        target.put(WEBHOOKS, webhooks);
    }

    @Override
    protected void $fromSubreddits(JSONObject source, TextChannel target){
        JSONArray subreddits = source.optJSONArray(SUBREDDITS);
        if(subreddits != null)
            for(int i = 0 ; i < subreddits.length() ; ++i)
                target.addSubreddits(subreddits.getString(i));
    }

    @Override
    protected void $toSubreddits(TextChannel source, JSONObject target){
        JSONArray subreddits = new JSONArray();
        for(String subreddit : source.getSubreddits())
            subreddits.put(subreddit);
        target.put(SUBREDDITS, subreddits);
    }
}
