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

import org.json.JSONObject;
import vartas.discord.blanc.Guild;
import vartas.discord.blanc.Role;
import vartas.discord.blanc.io.$json.JSONCredentials;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class JSONRole extends JSONRoleTOP {
    public static final String GROUP = "group";

    public static String getFileName(Role source){
        return getFileName(source.getId());
    }

    private static String getFileName(long id){
        return "r" + Long.toUnsignedString(id) + ".json";
    }

    private static Path getFileDirectory(Guild guild){
        return JSONCredentials.CREDENTIALS.getJsonDirectory().resolve(Long.toUnsignedString(guild.getId()));
    }

    public static Role fromJson(Role target, Guild guild, long id) throws IOException {
        Path filePath = getFileDirectory(guild).resolve(getFileName(id));
        return fromJson(target, filePath);
    }

    @Override
    protected void $fromGroup(JSONObject source, Role target){
        target.setGroup(Optional.ofNullable(source.optString(GROUP)));
    }

    @Override
    protected void $toGroup(Role source, JSONObject target){
        source.ifPresentGroup(group -> target.put(GROUP, group));
    }
}
