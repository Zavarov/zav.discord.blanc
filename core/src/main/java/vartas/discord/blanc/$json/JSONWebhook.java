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

package vartas.discord.blanc.$json;

import org.json.JSONArray;
import org.json.JSONObject;
import vartas.discord.blanc.Webhook;

public class JSONWebhook extends JSONWebhookTOP {
    private static final String SUBREDDITS = "subreddits";

    @Override
    protected void $fromMessages(JSONObject source, Webhook target){
        //Omitted
    }

    @Override
    protected void $toMessages(Webhook source, JSONObject target){
        //Omitted
    }

    @Override
    protected void $fromSubreddits(JSONObject source, Webhook target){
        JSONArray subreddits = source.optJSONArray(SUBREDDITS);

        if(subreddits != null) {
            for (int i = 0; i < subreddits.length(); ++i)
                target.addSubreddits(subreddits.getString(i));
        }
    }

    @Override
    protected void $toSubreddits(Webhook source, JSONObject target){
        JSONArray subreddits = new JSONArray();

        for(String subreddit : source.getSubreddits())
            subreddits.put(subreddit);

        target.put(SUBREDDITS, subreddits);
    }
}
