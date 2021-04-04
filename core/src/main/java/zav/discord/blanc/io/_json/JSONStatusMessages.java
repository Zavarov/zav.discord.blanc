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

package zav.discord.blanc.io._json;

import org.json.JSONArray;
import org.json.JSONObject;
import zav.discord.blanc.io.StatusMessages;

import javax.annotation.Nonnull;

public class JSONStatusMessages extends JSONStatusMessagesTOP {
    @Nonnull
    public static final StatusMessages STATUS_MESSAGES = new StatusMessages();

    @Override
    protected void $fromStatusMessages(JSONObject source, StatusMessages target){
        JSONArray jsonArray = source.getJSONArray("statusMessages");
        for(int i = 0 ; i < jsonArray.length() ; ++i){
            target.addStatusMessages(jsonArray.getString(i));
        }
    }
}
