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

package vartas.discord.blanc.io.$json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.Errors;
import vartas.discord.blanc.io.StatusMessages;

import java.io.IOException;
import java.nio.file.Paths;

public class JSONStatusMessages extends JSONStatusMessagesTOP {
    public static StatusMessages STATUS_MESSAGES = new StatusMessages();

    static{
        try{
            fromJson(STATUS_MESSAGES, Paths.get("status.json"));
        }catch(IOException e){
            LoggerFactory.getLogger(JSONStatusMessages.class.getSimpleName()).error(Errors.INVALID_FILE.toString(), e.toString());
        }
    }

    @Override
    protected void $fromStatusMessages(JSONObject source, StatusMessages target){
        JSONArray jsonArray = source.getJSONArray("statusMessages");
        for(int i = 0 ; i < jsonArray.length() ; ++i){
            target.addStatusMessages(jsonArray.getString(i));
        }
    }
}
