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
import vartas.discord.blanc.Guild;
import vartas.discord.blanc.io.$json.JSONCredentials;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class JSONGuild extends JSONGuildTOP {
    public static final String PREFIX = "prefix";
    public static final String BLACKLIST = "blacklist";

    public static String getFileName(Guild source){
        return getFileName(source.getId());
    }

    private static String getFileName(long id){
        return "g" + Long.toUnsignedString(id) + ".json";
    }

    private static Path getFileDirectory(long id){
        return JSONCredentials.CREDENTIALS.getJsonDirectory().resolve(Long.toUnsignedString(id));
    }

    public static Guild fromJson(Guild target, long id) throws IOException {
        Path filePath = getFileDirectory(id).resolve(getFileName(id));
        return fromJson(target, filePath);
    }

    @Override
    protected void $fromPrefix(JSONObject source, Guild target){
        target.setPrefix(Optional.ofNullable(source.optString(PREFIX)));
    }

    @Override
    protected void $toPrefix(Guild source, JSONObject target){
        source.ifPresentPrefix(prefix -> target.put(PREFIX, prefix));
    }

    @Override
    protected void $fromActivity(JSONObject source, Guild target){
        //Omitted
    }

    @Override
    protected void $toActivity(Guild source, JSONObject target){
        //Omitted
    }

    @Override
    protected void $fromBlacklist(JSONObject source, Guild target){
        JSONArray jsonBlacklist = source.optJSONArray(BLACKLIST);
        if(jsonBlacklist != null) {
            for (int i = 0; i < jsonBlacklist.length(); ++i) {
                target.addBlacklist(jsonBlacklist.getString(i));
            }
            target.compilePattern();
        }
    }

    @Override
    protected void $toBlacklist(Guild source, JSONObject target){
        JSONArray jsonBlacklist = new JSONArray();
        for(String entry : source.getBlacklist())
            jsonBlacklist.put(entry);
        target.put(BLACKLIST, jsonBlacklist);
    }
}
