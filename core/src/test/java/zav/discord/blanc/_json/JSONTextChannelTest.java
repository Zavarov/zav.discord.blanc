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

public class JSONTextChannelTest extends AbstractJSONTest{
    @Test
    public void testGetFileName(){
        assertThat(JSONTextChannel.getFileName(textChannel).equals(JSONTextChannel.getFileName(textChannel.getId())));
    }

    @Test
    public void testGetName(){
        assertThat(textChannel.getName()).isEqualTo("Reddit");
    }

    @Test
    public void testGetId(){
        assertThat(textChannel.getId()).isEqualTo(30);
    }

    @Test
    public void testGetSubreddits(){
        assertThat(textChannel.getSubreddits()).containsExactly("redditdev");
    }

    @Test
    public void testGetJsonObject(){
        JSONObject jsonObject = JSONTextChannel.toJson(textChannel, new JSONObject());
        assertEquals(jsonObject.toString(), jsonTextChannel.toString());
    }
}
