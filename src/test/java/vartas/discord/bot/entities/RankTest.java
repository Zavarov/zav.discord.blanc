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
import vartas.discord.bot.entities.offline.OfflineShard;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

public class RankTest extends AbstractTest {
    OfflineShard shard;
    Rank rank;
    JDAImpl jda;
    UserImpl user;
    @Before
    public void setUp(){
        shard = OfflineShard.create(null);
        jda = shard.createJda(0, null);
        user = new UserImpl(userId, jda);

        rank = new Rank();
        rank.add(user, Rank.Ranks.DEVELOPER);
        rank.add(user, Rank.Ranks.REDDIT);
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

    @Test
    public void visitorTest(){
        Rank.Visitor visitor = new Visitor();
        rank.accept(visitor);
        visitor = new EmptyVisitor();
        rank.accept(visitor);
    }

    private static class EmptyVisitor implements Rank.Visitor{}

    private class Visitor implements Rank.Visitor{
        @Override
        public void visit(@Nonnull Rank rank){
            assertThat(rank).isEqualTo(RankTest.this.rank);
        }
        @Override
        public void traverse(@Nonnull Rank rank){
            assertThat(rank).isEqualTo(RankTest.this.rank);
        }
        @Override
        public void endVisit(@Nonnull Rank rank){
            assertThat(rank).isEqualTo(RankTest.this.rank);
        }
    }
}
