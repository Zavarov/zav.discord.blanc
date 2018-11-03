/*
 * Copyright (C) 2017 u/Zavarov
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

package vartas.discordbot;

import com.google.common.io.Files;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import javax.security.auth.login.LoginException;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDA.Status;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.utils.JDALogger;
import org.slf4j.Logger;
import vartas.reddit.PushshiftWrapper;
import vartas.reddit.RedditBot;
import vartas.xml.XMLConfig;
import vartas.xml.XMLCredentials;

/**
 * This class represents the core instance of the bot. 
 * @author u/Zavarov
 */
public class DiscordRuntime extends ObjectArrayList<DiscordBot>{
    private static final long serialVersionUID = 1L;
    /**
     * The amount of time between the generation of each JDA.
     */
    protected static long SLEEP = 5000;
    /**
     * The configuration file.
     */
    protected final XMLConfig config;
    /**
     * The log for this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The instance that communicates with the Reddit API.
     */
    protected final RedditBot reddit;
    /**
     * The instance of the crawler that contains all data.
     */
    protected final PushshiftWrapper pushshift;
    /**
     * This function creates the underlying network adapter for all the Reddit API calls.
     */
    protected static Function<XMLCredentials,NetworkAdapter> ADAPTER = (c) -> new OkHttpNetworkAdapter(new UserAgent(
                c.getPlatform(),
                c.getAppid(),
                c.getVersion(),
                c.getUser()
        ));
    /**
     * The function that creates a new JDABuilder every time it is called.
     */
    protected static Function<XMLCredentials,JDABuilder> BUILDER = (c) -> new JDABuilder(AccountType.BOT)
        .setStatus(OnlineStatus.ONLINE)
        .setToken(c.getDiscordToken())
        .setAutoReconnect(true);
    /**
     * Creates a lightweight instance of this runtime that isn't connectend to
     * Discord and therefore also doesn't contain any instances for the shards.
     * @throws LoginException if the provided token was invalid.
     * @throws InterruptedException if the login process to Discord has been interrupted.
     * @throws IOException if the serial file couldn't be accessed.
     * @throws ClassNotFoundException if the serial object belongs to an unknown class.
     */
    public DiscordRuntime() throws LoginException, InterruptedException, IOException, ClassNotFoundException{
        config = XMLConfig.create(new File("config.xml"));
        XMLCredentials credentials = XMLCredentials.create(new File(String.format("%s/credentials.xml",config.getDataFolder())));
        int shards = config.getDiscordShards();
        
        reddit = new RedditBot(credentials, ADAPTER.apply(credentials));
        pushshift = new PushshiftWrapper(reddit);
        pushshift.read();
        
        JDABuilder jda = BUILDER.apply(credentials);
        
        DiscordRuntime.this.createGuildsFolder(config);
        for(int i = 0 ; i < config.getDiscordShards() ; ++i){
            add(DiscordRuntime.this.createDiscordBot(i,jda,config));
        }
        removeOldFiles(config);
        log.info("Initialization finished.");
    }
    /**
     * Creates a guilds folder in the configuration folder if it doesn't exist.
     * @param config the configuration file.
     */
    protected void createGuildsFolder(XMLConfig config){
        File guilds = new File(String.format("%s/guilds",config.getDataFolder()));
        //The guilds folder might not exist when the repository was cloned.
        guilds.mkdirs();
    }
    /**
     * Creates the bot instance for the specific shard.
     * it to the runtime.
     * @param shard the shard of the JDA.
     * @param builder the builder for generating the JDA.
     * @param config the configuration file.
     * @throws LoginException if the provided token was invalid.
     * @throws InterruptedException if the login process to Discord has been interrupted.
     * @return the bot instance for the specified shard.
     */
    protected DiscordBot createDiscordBot(int shard, JDABuilder builder, XMLConfig config) throws LoginException, InterruptedException{
        JDA jda = builder
                .useSharding(shard, config.getDiscordShards())
                .build().awaitStatus(Status.AWAITING_LOGIN_CONFIRMATION);
        //5 seconds for the limiter
        Thread.sleep(SLEEP);
        return new DiscordBot(DiscordRuntime.this,jda,config, reddit, pushshift);
    }
    /**
     * Removes all configuration files of the guilds the bot doesn't have access to anymore.
     * @param config the configuration file that contains the path to the server files.
     */
    private void removeOldFiles(XMLConfig config){
        File[] files = new File(String.format("%s/guilds",config.getDataFolder())).listFiles();
        for(File file : files){
            long id = Long.parseLong(Files.getNameWithoutExtension(file.getName()));
            DiscordBot bot = getBot(id);
            Guild guild = bot.getJda().getGuildById(id);
            //The bot was kicked from the guild.
            if(guild == null){
                file.delete();
            }
        }
    }
    /**
     * Computes the shard of the given guild. 
     * The shard is (guild.getId() >> 22) % #shards
     * @param id the id of the guild.
     * @return returns the bot that is responsible for the given id.
     */
    public DiscordBot getBot(long id){
        return get((int)((id >> 22) % config.getDiscordShards()));
    }
    /**
     * Terminates all threads that are a part of this entity.
     */
    public void shutdown(){
        this.removeIf(e -> { e.shutdown() ; return true;});
        log.info("Runtime terminated.");
    }
}