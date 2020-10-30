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

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.Errors;
import vartas.discord.blanc.io.Credentials;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Paths;

public class JSONCredentials extends JSONCredentialsTOP {
    @Nonnull
    public static Credentials CREDENTIALS = new Credentials();

    static{
        try{
            fromJson(CREDENTIALS, Paths.get("credentials.json"));
        }catch(IOException e){
            LoggerFactory.getLogger(JSONCredentials.class.getSimpleName()).error(Errors.INVALID_FILE.toString(), e.toString());
        }
    }

    @Override
    protected void $fromGuildDirectory(JSONObject source, Credentials target){
        target.setGuildDirectory(Paths.get(source.getString("guildDirectory")));
    }
}
