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

package zav.discord.blanc._json;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONGuildTest extends AbstractJSONTest{
    @Test
    public void testGetFileName(){
        assertThat(JSONGuild.getFileName(guild).equals(JSONGuild.getFileName(guild.getId())));
    }

    @Test
    public void testGetBlacklist(){
        assertThat(guild.getBlacklist()).containsExactly("handholding");
    }

    @Test
    public void testGetPrefix(){
        assertThat(guild.getPrefix()).contains("b.");
    }

    @Test
    public void testGetName(){
        assertThat(guild.getName()).isEqualTo("Guild");
    }

    @Test
    public void testGetId(){
        assertThat(guild.getId()).isEqualTo(10);
    }


    @Test
    public void testGetJsonObject(){
        JSONObject jsonObject = JSONGuild.toJson(guild, new JSONObject());
        assertEquals(jsonObject.toString(), jsonGuild.toString());
    }
}
