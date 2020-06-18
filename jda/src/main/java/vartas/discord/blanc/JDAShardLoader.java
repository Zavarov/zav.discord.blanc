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

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.RateLimiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import org.apache.commons.lang3.concurrent.TimedSemaphore;
import vartas.discord.blanc.command.CommandBuilder;
import vartas.discord.blanc.factory.ShardFactory;
import vartas.discord.blanc.io.Credentials;
import vartas.discord.blanc.listener.GuildCommandListener;
import vartas.discord.blanc.listener.GuildMessageListener;
import vartas.discord.blanc.listener.PrivateCommandListener;
import vartas.discord.blanc.visitor.RedditVisitor;
import vartas.reddit.Client;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.concurrent.TimeUnit;

public class JDAShardLoader extends ShardLoader{
    /**
     * The minimum amount of time between connecting multiple JDA instances.
     */
    @Nonnull
    private final TimedSemaphore rateLimiter = new TimedSemaphore(5, TimeUnit.SECONDS, 1);
    /**
     * The builder for creating one JDA instance for each shard.
     */
    @Nonnull
    private final JDABuilder jdaBuilder;
    /**
     * Responsible for communicating with the Reddit API. All shards share the same client, to utilize make the most out
     * of caching requested submissions.
     */
    @Nonnull
    private final Client redditClient;
    /**
     * The builder for transforming the received messages into executable commands.
     */
    @Nonnull
    private final CommandBuilder commandBuilder;
    /**
     * The total number of shards. This variable is required to determine which guilds belong to this shard.
     */
    private final int shardCount;

    public JDAShardLoader(@Nonnull Credentials credentials, @Nonnull CommandBuilder commandBuilder) {
        super(credentials);
        this.shardCount = credentials.getShardCount();
        this.jdaBuilder = new JDABuilder(credentials.getDiscordToken()).setStatus(OnlineStatus.ONLINE);
        this.commandBuilder = commandBuilder;
        this.redditClient = new vartas.reddit.JrawClient(
                credentials.getRedditAccount(),
                credentials.getVersion(),
                credentials.getRedditId(),
                credentials.getRedditSecret()
        );
    }

    @Override
    public Shard load(int shardId){
        try {
            rateLimiter.acquire();

            JDA jda = jdaBuilder.useSharding(shardId, shardCount).build();
            RedditVisitor redditVisitor = createRedditVisitor(jda);
            Shard shard = ShardFactory.create(() -> new JDAShard(redditVisitor, jda), shardId);

            setGuildFunction(jda);
            shard.accept(this);

            //Load listeners
            jda.addEventListener(new GuildCommandListener(commandBuilder, shard));
            jda.addEventListener(new PrivateCommandListener(commandBuilder));
            jda.addEventListener(new GuildMessageListener(shard));

            return shard;
        } catch(LoginException | InterruptedException e) {
            //TODO Error Messages;
            throw new RuntimeException();
        }
    }

    private void setGuildFunction(JDA jda){
        super.defaultGuild = (guildId) -> {
            //TODO Error Messages
            net.dv8tion.jda.api.entities.Guild guild = jda.getGuildById(guildId);
            System.out.println(jda.getGuilds());
            System.out.println(guildId);
            Preconditions.checkNotNull(guild, new NullPointerException(Long.toUnsignedString(guildId)));
            return JDAGuild.create(guild);
        };
    }

    private RedditVisitor createRedditVisitor(JDA jda){
        ServerHookPoint discordHook = new JDAServerHookPoint(jda);
        return new RedditVisitor(discordHook, redditClient);
    }
}
