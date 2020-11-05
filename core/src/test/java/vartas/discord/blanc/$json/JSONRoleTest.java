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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONRoleTest extends AbstractJSONTest{
    @Test
    public void testGetGroup(){
        assertThat(role.getGroup()).contains("Color");
    }

    @Test
    public void testGetName(){
        assertThat(role.getName()).isEqualTo("Purple");
    }

    @Test
    public void testGetId(){
        assertThat(role.getId()).isEqualTo(20);
    }

    @Test
    public void testGetJsonObject(){
        JSONObject jsonObject = JSONRole.toJson(role, new JSONObject());
        assertEquals(jsonObject.toString(), jsonRole.toString());
    }
}
