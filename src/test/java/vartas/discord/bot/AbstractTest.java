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

package vartas.discord.bot;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.internal.entities.UserImpl;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import vartas.discord.bot.entities.Credentials;
import vartas.discord.bot.entities.Rank;
import vartas.discord.bot.entities.Status;
import vartas.discord.bot.entities.offline.OfflineCluster;
import vartas.discord.bot.entities.offline.OfflineCommandBuilder;
import vartas.discord.bot.entities.offline.OfflineShard;
import vartas.discord.bot.visitor.ShardVisitor;

import javax.security.auth.login.LoginException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractTest {
    public static final Path CREDENTIALS = Paths.get("src/test/resources/credentials.json");
    public static final Path STATUS = Paths.get("src/test/resources/status.json");
    public static final Path RANK = Paths.get("src/test/resources/rank.json");
    public static final Path GUILDS = Paths.get("src/test/resources/guilds");
    protected static AuthorizationConfig authorization = new AuthorizationConfig(AccountType.BOT, "12345");
    protected static OfflineShard shard;
    protected static OfflineCluster cluster;
    protected static long guildId = 0L;
    protected static long roleId = 2L;
    protected static long channelId = 1L;
    protected static long userId = 3L;
    protected Rank rank;
    protected Status status;
    protected Credentials credentials;
    protected EntityAdapter entityAdapter;
    protected OfflineCommandBuilder builder;

    static {
        try {
            shard = new OfflineShard();
            cluster = OfflineShard.cluster;
            cluster.registerShard(shard);
            cluster.shutdown();
        }catch(LoginException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    @Before
    public void initJda() {
        entityAdapter = new JSONEntityAdapter(CREDENTIALS, STATUS, RANK, GUILDS);
        rank = entityAdapter.rank();
        status = entityAdapter.status();
        credentials = entityAdapter.credentials();

        builder = new OfflineCommandBuilder();
    }

    @After
    public void tearDown(){
        shard.send.clear();
        shard.removed.clear();
        shard.stored.clear();
    }

    protected void addRank(UserImpl user, Rank.Ranks ranks){
        shard.accept(new ShardVisitor() {
            @Override
            public void visit(@NotNull Rank rank) {
                rank.add(user, ranks);
            }
        });
    }

    protected void removeRank(UserImpl user, Rank.Ranks ranks){
        shard.accept(new ShardVisitor() {
            @Override
            public void visit(@NotNull Rank rank) {
                rank.remove(user, ranks);
            }
        });
    }

    protected void checkRank(UserImpl user, Rank.Ranks ranks, boolean expected){
        shard.accept(new ShardVisitor() {
            @Override
            public void visit(@NotNull Rank rank) {
                assertThat(rank.resolve(user, ranks)).isEqualTo(expected);
            }
        });
    }
}
