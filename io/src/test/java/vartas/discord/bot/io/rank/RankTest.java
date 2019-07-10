package vartas.discord.bot.io.rank;

import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Copyright (C) 2019 Zavarov
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
public class RankTest {
    RankConfiguration ranks;

    @Before
    public void setUp(){
        String source = "src/test/resources/rank.perm";
        File reference = new File("target/test/resources/rank.perm");
        ranks = RankHelper.parse(source, reference);
    }

    @Test
    public void testPermissions(){
        Multimap<Long, RankType> multimap = ranks.getRanks();
        assertThat(multimap.size()).isEqualTo(2);
        assertThat(multimap.get(1L)).containsExactlyInAnyOrder(RankType.ROOT, RankType.REDDIT);
    }

    @Test
    public void testUpdate(){
        String source = "target/test/resources/rank.perm";
        File reference = new File("target/test/resources/rank.perm");
        ranks = RankHelper.parse(source, reference);

        testPermissions();
    }
}
