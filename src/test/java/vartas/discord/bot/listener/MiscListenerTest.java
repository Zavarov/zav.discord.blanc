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

package vartas.discord.bot.listener;

import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;
import vartas.discord.bot.entities.offline.OfflineCluster;
import vartas.discord.bot.entities.offline.OfflineShard;

public class MiscListenerTest extends AbstractTest {
    OfflineCluster cluster;
    OfflineShard shard;
    JDAImpl jda;
    GuildImpl guild;

    MiscListener listener;
    GuildLeaveEvent event;
    @Before
    public void setUp(){
        cluster = OfflineCluster.create();
        shard = OfflineShard.create(cluster);
        jda = new JDAImpl(Authorization);
        guild = new GuildImpl(jda, guildId);
        listener = new MiscListener(shard);
        event = new GuildLeaveEvent(jda, 12345L, guild);

        cluster.registerShard(shard);
        shard.guilds.put(guildId, guild);
    }

    @Test
    public void onGuildLeaveTest(){
        listener.onGuildLeave(event);
        //TODO
        //assertThat(cluster.Adapter.removed).containsExactly(guildId);
    }
}
