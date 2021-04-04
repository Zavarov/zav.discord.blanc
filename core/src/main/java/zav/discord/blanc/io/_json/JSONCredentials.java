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

import org.json.JSONObject;
import zav.discord.blanc.io.Credentials;

import javax.annotation.Nonnull;
import java.nio.file.Paths;

public class JSONCredentials extends JSONCredentialsTOP {
    @Nonnull
    public static final Credentials CREDENTIALS = new Credentials();

    @Override
    protected void $fromJsonDirectory(JSONObject source, Credentials target){
        target.setJsonDirectory(Paths.get(source.getString("jsonDirectory")));
    }
}
