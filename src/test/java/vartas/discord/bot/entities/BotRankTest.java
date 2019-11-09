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

import org.junit.Test;
import vartas.discord.bot.AbstractBotTest;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class BotRankTest extends AbstractBotTest {
    Path target = Paths.get("src/test/resources/Rank.txt");
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
    public void cleanTest(){
        userMap.clear();

        assertThat(rank.resolve(user, BotRank.Type.REDDIT)).isTrue();
        assertThat(rank.resolve(user, BotRank.Type.DEVELOPER)).isTrue();

        rank.clean();

        assertThat(rank.resolve(user, BotRank.Type.REDDIT)).isFalse();
        assertThat(rank.resolve(user, BotRank.Type.DEVELOPER)).isFalse();
    }

    @Test
    public void toStringTest(){
        assertThat(target).hasContent(rank.toString());
    }
}
