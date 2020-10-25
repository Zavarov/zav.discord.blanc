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
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.Role;
import vartas.discord.blanc.TextChannel;
import vartas.discord.blanc.Webhook;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONGuildTest extends AbstractJSONTest{
    @Test
    public void testGetBlacklist(){
        assertThat(GUILD.getBlacklist()).containsExactly("handholding");
    }

    @Test
    public void testGetPrefix(){
        assertThat(GUILD.getPrefix()).contains(JSON_GUILD.getString(JSONGuild.PREFIX));
    }

    @Test
    public void testGetRoles(){
        List<Role> roles = new ArrayList<>(GUILD.valuesRoles());

        assertEquals(roles.size(), 1);

        Role role = roles.get(0);
        assertThat(role.getId()).isEqualTo(22222222222222L);
        assertThat(role.getName()).isEqualTo("Banana");
        assertThat(role.getGroup()).contains("Fruit");
    }

    @Test
    public void testGetChannels(){
        List<TextChannel> textChannels = new ArrayList<>(GUILD.valuesChannels());

        assertEquals(textChannels.size(), 1);

        TextChannel textChannel = textChannels.get(0);
        assertThat(textChannel.getId()).isEqualTo(33333333333333L);
        assertThat(textChannel.getName()).isEqualTo("redditdev");
        assertThat(textChannel.getSubreddits()).containsExactly("redditdev");

        List<Webhook> webhooks = new ArrayList<>(textChannel.valuesWebhooks());
        assertEquals(webhooks.size(), 1);

        Webhook webhook = webhooks.get(0);
        assertEquals(webhook.getId(), 44444444444444L);
        assertEquals(webhook.getName(), "redditdev");
    }

    @Test
    public void testGetJsonObject(){
        JSONObject jsonGuild = JSONGuild.toJson(GUILD, new JSONObject());
        assertThat(JSON_GUILD.similar(jsonGuild));
    }
}
