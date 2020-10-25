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

import org.json.JSONObject;
import vartas.discord.blanc.Role;

import java.util.Optional;

public class JSONRole extends JSONRoleTOP {
    public static final String GROUP = "group";

    @Override
    protected void $fromGroup(JSONObject source, Role target){
        target.setGroup(Optional.ofNullable(source.optString(GROUP)));
    }

    @Override
    protected void $toGroup(Role source, JSONObject target){
        source.ifPresentGroup(group -> target.put(GROUP, group));
    }
}
