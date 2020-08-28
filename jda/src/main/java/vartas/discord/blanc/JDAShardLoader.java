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
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.command.CommandBuilder;
import vartas.discord.blanc.factory.ShardFactory;
import vartas.discord.blanc.io.Credentials;
import vartas.discord.blanc.json.JSONGuild;
import vartas.discord.blanc.listener.BlacklistListener;
import vartas.discord.blanc.listener.GuildCommandListener;
import vartas.discord.blanc.listener.GuildMessageListener;
import vartas.discord.blanc.listener.PrivateCommandListener;
import vartas.discord.blanc.visitor.RedditVisitor;
import vartas.reddit.Client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class JDAShardLoader extends ShardLoader{
    /**
     * The minimum amount of time between connecting multiple JDA instances is 5 seconds.<br>
     * We use an additional second as buffer, bringing the time up to 6 seconds.
     */
    @Nonnull
    private static final TimedSemaphore rateLimiter = new TimedSemaphore(6, TimeUnit.SECONDS, 1);
    /**
     * The builder for creating one JDA instance for each shard.
     */
    @Nonnull
    private final ShardManager jdaBuilder;
    /**
     * The builder for transforming the received messages into executable commands.
     */
    @Nonnull
    private final BiFunction<Shard, JDA, CommandBuilder> commandBuilderFunction;
    /**
     * The JDA instance of the currently visited shard.
     */
    @Nullable
    private JDA currentJda;

    @Nonnull
    private final RedditVisitor redditVisitor;

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public JDAShardLoader(@Nonnull Credentials credentials, @Nonnull BiFunction<Shard, JDA, CommandBuilder> commandBuilderFunction) {
        super(credentials);
        this.commandBuilderFunction = commandBuilderFunction;
        try {
            this.jdaBuilder = DefaultShardManagerBuilder.createDefault(credentials.getDiscordToken())
                    .setStatus(OnlineStatus.ONLINE)
                    .setShardsTotal(credentials.getShardCount())
                    .build();
        } catch(LoginException e) {
            //TODO Error Messages;
            throw new RuntimeException();
        }
        Client redditClient = new vartas.reddit.JrawClient(
                credentials.getRedditAccount(),
                credentials.getVersion(),
                credentials.getRedditId(),
                credentials.getRedditSecret()
        );
        this.redditVisitor = new RedditVisitor(redditClient);
    }

    @Override
    public Shard load(int shardId){
        try {
            rateLimiter.acquire();

            currentJda = Preconditions.checkNotNull(jdaBuilder.getShardById(shardId)).awaitReady();

            Shard shard;
            SelfUser selfUser = JDASelfUser.create(currentJda.getSelfUser());
            //Only the master shard has to modify the status messages
            if(shardId == 0) {
                StatusMessageRunnable statusMessageRunnable = createStatusMessageRunnable(selfUser);
                shard = ShardFactory.create(() -> new JDAShard(redditVisitor, statusMessageRunnable, currentJda), shardId, selfUser);
            }else{
                shard = ShardFactory.create(() -> new JDAShard(redditVisitor, currentJda), shardId, selfUser);
            }

            setGuildFunction(currentJda);
            shard.accept(this);

            //Load listeners
            CommandBuilder commandBuilder = commandBuilderFunction.apply(shard, currentJda);
            currentJda.addEventListener(new GuildCommandListener(commandBuilder, shard));
            currentJda.addEventListener(new PrivateCommandListener(commandBuilder, shard));
            currentJda.addEventListener(new GuildMessageListener(shard));
            currentJda.addEventListener(new BlacklistListener(shard));

            return shard;
        } catch( InterruptedException e) {
            //TODO Error Messages;
            throw new RuntimeException();
        }
    }

    @Override
    public Optional<Guild> load(@Nonnull Path guildPath) {
        try{
            log.info("Loading guild {}.", guildPath);
            JSONObject jsonGuild = new JSONObject(Files.readString(guildPath));
            long guildId = jsonGuild.getLong(JSONGuild.ID);

            if(currentJda != null) {
                net.dv8tion.jda.api.entities.Guild jdaGuild = currentJda.getGuildById(guildId);
                if (jdaGuild != null)
                    return Optional.of(JDAGuild.create(jdaGuild, jsonGuild));
            }

            return Optional.empty();
        }catch(IOException e){
            log.error(Errors.INVALID_FILE.toString());
            return Optional.empty();
        }catch(JSONException e){
            log.error(Errors.INVALID_JSON_FILE.toString());
            return Optional.empty();
        }
    }

    private void setGuildFunction(JDA jda){
        super.defaultGuild = (guildId) -> {
            //TODO Error Messages
            net.dv8tion.jda.api.entities.Guild guild = jda.getGuildById(guildId);
            Preconditions.checkNotNull(guild, new NullPointerException(Long.toUnsignedString(guildId)));
            return JDAGuild.create(guild);
        };
    }

    private StatusMessageRunnable createStatusMessageRunnable(SelfUser selfUser){
        return new StatusMessageRunnable(selfUser);
    }
}
