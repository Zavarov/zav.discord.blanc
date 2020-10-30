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

package vartas.discord.blanc;

import com.google.common.cache.Cache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.$json.JSONGuild;
import vartas.discord.blanc.mock.GuildMock;
import vartas.discord.blanc.mock.ShardLoaderMock;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ShardLoaderTest extends AbstractTest {
    public ShardLoader shardLoader;
    public Shard shard;
    public Guild guild;
    public Cache<Long, Guild> guilds;
    public Path guildDirectory;
    public Path notADirectory;
    public Path notAGuild;
    @BeforeEach
    public void setUp() throws IOException {
        guild = JSONGuild.fromJson(new GuildMock(), RESOURCES.resolve("guild.json"));
        Shard.write(guild);

        shardLoader = new ShardLoaderMock(credentials);
        shard = shardLoader.load(0);
        guilds = shard.getGuilds();

        notADirectory = RESOURCES.resolve("notadirectory");
        notAGuild = RESOURCES.resolve("invalid");
        guildDirectory = credentials.getGuildDirectory();
    }

    @AfterEach
    public void tearDown(){
        credentials.setGuildDirectory(guildDirectory);
    }

    @Test
    public void testLoad(){
        assertThat(shard.getId()).isEqualTo(0);
        assertThat(guilds.size()).isEqualTo(1);
    }

    @Test
    public void testInvalidDirectory(){
        shard.invalidateAllGuilds();
        credentials.setGuildDirectory(notADirectory);

        shardLoader.load(0);
        assertThat(shard.valuesGuilds()).isEmpty();
    }

    @Test
    public void testInvalidFile(){
        credentials.setGuildDirectory(notAGuild);
        shard = shardLoader.load(0);
        guilds = shard.getGuilds();

        assertThat(shard.getId()).isEqualTo(0);
        assertThat(guilds.size()).isEqualTo(0);
    }

    @Test
    public void testVisit(){
        Map<Long, Guild> guilds;

        shard.accept(shardLoader);
        guilds = shard.getGuilds().asMap();
        assertThat(guilds).hasSize(1);
        assertThat(guilds.containsKey(guild.getId()));
        assertThat(guilds.get(guild.getId()).valuesChannels()).hasSize(1);
        assertThat(guilds.get(guild.getId()).valuesRoles()).hasSize(1);
    }
}
