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
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.UserImpl;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RankTest extends AbstractTest {
    JDAImpl jda;
    UserImpl user;
    @Before
    public void setUp(){
        jda = shard.createJda();
        user = new UserImpl(userId, jda);
    }

    @Test
    public void resolveTest(){
        assertThat(rank.resolve(user, Rank.Ranks.REDDIT)).isTrue();
        assertThat(rank.resolve(user, Rank.Ranks.ROOT)).isFalse();
        assertThat(rank.resolve(user, Rank.Ranks.DEVELOPER)).isTrue();
    }

    @Test
    public void addTest(){
        assertThat(rank.resolve(user, Rank.Ranks.ROOT)).isFalse();
        rank.add(user, Rank.Ranks.ROOT);
        assertThat(rank.resolve(user, Rank.Ranks.ROOT)).isTrue();
    }

    @Test
    public void removeTest(){
        assertThat(rank.resolve(user, Rank.Ranks.REDDIT)).isTrue();
        rank.remove(user, Rank.Ranks.REDDIT);
        assertThat(rank.resolve(user, Rank.Ranks.REDDIT)).isFalse();
    }

    @Test
    public void getTest(){
        Multimap<Long, Rank.Ranks> multimap = rank.get();

        assertThat(multimap.size()).isEqualTo(2);
        assertThat(multimap.containsKey(user.getIdLong())).isTrue();
        assertThat(multimap.get(user.getIdLong())).containsExactlyInAnyOrder(Rank.Ranks.REDDIT, Rank.Ranks.DEVELOPER);
    }

    @Test
    public void typeGetNameTest(){
        assertThat(Rank.Ranks.DEVELOPER.getName()).isEqualTo("Developer");
        assertThat(Rank.Ranks.REDDIT.getName()).isEqualTo("Reddit");
        assertThat(Rank.Ranks.ROOT.getName()).isEqualTo("Root");
    }
}
