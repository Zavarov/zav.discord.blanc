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
import zav.discord.blanc.Rank;
import zav.discord.blanc.io.Ranks;

import javax.annotation.Nonnull;

public class JSONRanks extends JSONRanksTOP {
    @Nonnull
    public static final Ranks RANKS = new Ranks();

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
