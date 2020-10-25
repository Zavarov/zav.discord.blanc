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

package vartas.discord.blanc.io.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.Errors;
import vartas.discord.blanc.Rank;
import vartas.discord.blanc.io.Ranks;

import java.io.IOException;
import java.nio.file.Paths;

public class JSONRanks extends JSONRanksTOP {
    public static Ranks RANKS = new Ranks();

    static{
        try{
            fromJson(RANKS, Paths.get("ranks.json"));
        }catch(IOException e){
            LoggerFactory.getLogger(JSONRanks.class.getSimpleName()).error(Errors.INVALID_FILE.toString(), e.toString());
        }
    }

    protected void $fromRanks(JSONObject source, Ranks target){
        for(String key : source.keySet()){
            JSONArray values = source.getJSONArray(key);
            for(int i = 0 ; i < values.length() ; ++i)
                target.putRanks(Long.parseUnsignedLong(key), values.getEnum(Rank.class, i));
        }
    }

    protected void $toRanks(Ranks source, JSONObject target){
        source.asMapRanks().forEach((key, values) -> {
            JSONArray jsonRanks = new JSONArray();
            values.forEach(jsonRanks::put);
            target.put(Long.toUnsignedString(key), jsonRanks);
        });
    }
}
