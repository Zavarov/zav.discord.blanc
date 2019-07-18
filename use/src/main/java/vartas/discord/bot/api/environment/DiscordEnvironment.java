/*
 * Copyright (C) 2018 u/Zavarov
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
package vartas.discord.bot.api.environment;

import com.google.common.io.Files;
import com.google.common.util.concurrent.RateLimiter;
import de.monticore.symboltable.GlobalScope;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import org.apache.commons.io.FileUtils;
import vartas.discord.bot.api.communicator.DiscordCommunicator;
import vartas.discord.bot.exec.AbstractCommandBuilder;
import vartas.discord.bot.io.config.ConfigHelper;
import vartas.discord.bot.io.guild._symboltable.GuildLanguage;
import vartas.discord.bot.io.rank.RankConfiguration;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class allows the program to communicate to the actual JDA API.
 */
public class DiscordEnvironment extends AbstractEnvironment{
    /**
     * We are only allowed to create a new JDA instance every 5 seconds.
     */
    protected static RateLimiter limiter = RateLimiter.create(1/5.0);
    /**
     * Creates an instance with the configuration file in the main directory.
     * @param commands the scope for all valid commands
     * @param builder the builder for generating the commands from the calls
     * @throws LoginException if the login failed.
     * @throws InterruptedException if the login process was interrupted.
     */
    public DiscordEnvironment(GlobalScope commands, Supplier<AbstractCommandBuilder> builder) throws LoginException, InterruptedException{
        super(ConfigHelper.parse("config.cfg"), commands, builder);

        addShards();
        removeOldGuilds();
        addRedditFeeds();
        
        //Update the game AFTER all shards have been loaded.
        super.tracker.run();
    }
    /**
     * Creates a communicator for every shard.
     * @throws LoginException if the login failed.
     * @throws InterruptedException if the login process was interrupted.
     */
    public final void addShards() throws LoginException, InterruptedException{
        for(int i = 0 ; i < config.getDiscordShards() ; ++i){
            limiter.acquire();
            shards.add(new DiscordCommunicator(this, create(i), commands, builder.get()));
        }
    }
    /**
     * Remove old server files that don't belong to a guild the bot is in anymore.
     */
    public final void removeOldGuilds(){
        File guilds = new File("guilds");
        guilds.mkdirs();
        
        //Collect the ids of all guilds
        Set<String> names = guilds().stream().map(Guild::getId).collect(Collectors.toSet());
        
        //Remove all files that aren't in the guild space
        for(File file : FileUtils.listFiles(guilds, new String[]{GuildLanguage.GUILD_FILE_ENDING}, false))
            if(!names.contains(Files.getNameWithoutExtension(file.getName())))
                file.delete();
    }
    /**
     * Adds the feeds from the guild files to the Reddit instance.
     */
    public final void addRedditFeeds(){
        guilds().forEach(guild ->
            feed.addSubreddits(communicator(guild).config(guild), guild)
        );
    }
    /**
     * Creates a new instance of the JDA that manages the specified shard.
     * @param shard the shard number.
     * @return a JDA instance in that shard.
     * @throws LoginException if the login failed.
     * @throws InterruptedException if the login process was interrupted.
     */
    private JDA create(int shard) throws LoginException, InterruptedException{
        return new JDABuilder()
                .setStatus(OnlineStatus.ONLINE)
                .setToken(config().getDiscordToken())
                .setAutoReconnect(true)
                .useSharding(shard, config().getDiscordShards())
                .build()
                .awaitStatus(JDA.Status.CONNECTED);
    }

    @Override
    public RankConfiguration rank() {
        return null;
    }
}
