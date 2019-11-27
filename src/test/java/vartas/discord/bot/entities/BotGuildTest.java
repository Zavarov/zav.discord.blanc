/*
 * Copyright (c) 2019 Zavarov
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

package vartas.discord.bot.entities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class BotGuildTest extends AbstractTest {
    BotGuild config;
    @Before
    public void setUp(){
        config = adapter.guild(guild, communicator);
    }

    @After
    public void tearDown(){
        config.remove("test", channel);
        config.remove("test", role);

        config.set("prefix");
        config.set(Pattern.compile("pattern"));
        config.add("channel", channel);
        config.add("role", role);
        config.store();
    }

    @Test
    public void getIdTest(){
        assertThat(config.getId()).isEqualTo(guild.getId());
    }

    @Test
    public void getBlacklistTest(){
        assertThat(config.blacklist()).map(Pattern::pattern).contains("pattern");
    }

    @Test
    public void setBlacklistTest(){
        assertThat(config.blacklist()).map(Pattern::pattern).contains("pattern");
        config.set(Pattern.compile("test"));
        assertThat(config.blacklist()).map(Pattern::pattern).contains("test");
    }

    @Test
    public void removeBlacklistTest(){
        Pattern pattern = null;
        assertThat(config.blacklist()).map(Pattern::pattern).contains("pattern");
        config.set(pattern);
        assertThat(config.blacklist()).isEmpty();
    }

    @Test
    public void getPrefixTest(){
        assertThat(config.prefix()).contains("prefix");
    }

    @Test
    public void setPrefixTest(){
        assertThat(config.prefix()).contains("prefix");
        config.set("test");
        assertThat(config.prefix()).contains("test");
    }

    @Test
    public void removePrefixTest(){
        String prefix = null;
        assertThat(config.prefix()).contains("prefix");
        config.set(prefix);
        assertThat(config.prefix()).isEmpty();
    }

    @Test
    public void resolveRoleTest(){
        assertThat(config.resolve(role)).containsExactly("role");
        assertThat(config.resolve("role",role)).isTrue();
        assertThat(config.resolve("test",role)).isFalse();
    }

    @Test
    public void removeRoleTest(){
        assertThat(config.resolve("role", role)).isTrue();
        config.remove("role", role);
        assertThat(config.resolve("role", role)).isFalse();
    }

    @Test
    public void addRoleTest(){
        assertThat(config.resolve("test", role)).isFalse();
        config.add("test", role);
        assertThat(config.resolve("test", role)).isTrue();
    }

    @Test
    public void resolveChannelTest(){
        assertThat(config.resolve(channel)).containsExactly("channel");
        assertThat(config.resolve("channel",channel)).isTrue();
        assertThat(config.resolve("test",channel)).isFalse();
    }

    @Test
    public void removeChannelTest(){
        assertThat(config.resolve("channel", channel)).isTrue();
        config.remove("channel", channel);
        assertThat(config.resolve("channel", channel)).isFalse();
    }

    @Test
    public void addChannelTest(){
        assertThat(config.resolve("test", channel)).isFalse();
        config.add("test", channel);
        assertThat(config.resolve("test", channel)).isTrue();
    }
}
