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

import com.google.common.collect.Multimap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BotRankTest extends AbstractTest {
    BotRank rank;

    @Before
    public void setUp(){
        rank = adapter.rank();
    }

    @After
    public void tearDown(){
        rank.add(user, BotRank.Type.DEVELOPER);
        rank.add(user, BotRank.Type.REDDIT);
        rank.remove(user, BotRank.Type.ROOT);
        rank.store();
    }

    @Test
    public void resolveTest(){
        assertThat(rank.resolve(user, BotRank.Type.REDDIT)).isTrue();
        assertThat(rank.resolve(user, BotRank.Type.ROOT)).isFalse();
        assertThat(rank.resolve(user, BotRank.Type.DEVELOPER)).isTrue();
    }

    @Test
    public void addTest(){
        assertThat(rank.resolve(user, BotRank.Type.ROOT)).isFalse();
        rank.add(user, BotRank.Type.ROOT);
        assertThat(rank.resolve(user, BotRank.Type.ROOT)).isTrue();
    }

    @Test
    public void removeTest(){
        assertThat(rank.resolve(user, BotRank.Type.REDDIT)).isTrue();
        rank.remove(user, BotRank.Type.REDDIT);
        assertThat(rank.resolve(user, BotRank.Type.REDDIT)).isFalse();
    }

    @Test
    public void getTest(){
        Multimap<Long, BotRank.Type> multimap = rank.get();

        assertThat(multimap.size()).isEqualTo(2);
        assertThat(multimap.containsKey(user.getIdLong())).isTrue();
        assertThat(multimap.get(user.getIdLong())).containsExactlyInAnyOrder(BotRank.Type.REDDIT, BotRank.Type.DEVELOPER);
    }

    @Test
    public void typeGetNameTest(){
        assertThat(BotRank.Type.DEVELOPER.getName()).isEqualTo("Developer");
        assertThat(BotRank.Type.REDDIT.getName()).isEqualTo("Reddit");
        assertThat(BotRank.Type.ROOT.getName()).isEqualTo("Root");
    }
}
