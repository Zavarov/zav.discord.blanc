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
package vartas.discordbot.comm;

import com.google.common.io.Files;
import com.google.common.util.concurrent.RateLimiter;
import java.io.File;
import java.util.Arrays;
import java.util.Set;
import static java.util.concurrent.TimeUnit.MINUTES;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.security.auth.login.LoginException;
import net.dean.jraw.http.NetworkAdapter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import vartas.discordbot.MessageListener;
import vartas.xml.XMLConfig;

/**
 * This class allows the program to communicate to the actual JDA API.
 * @author u/Zavarov
 */
public class DefaultEnvironment extends AbstractEnvironment{
    protected Supplier<JDABuilder> builder;
    /**
     * We are only allowed to create a new JDA instance every 5 minutes.
     */
    protected RateLimiter limiter = RateLimiter.create(MINUTES.toSeconds(5));
    /**
     * Creates an instance with the configuration file in the main directory.
     * @param builder the supplier for the builder that creates the JDA instances.
     * @param adapter the underlying network adapter for the Reddit API calls
     * @throws LoginException if the login failed.
     * @throws InterruptedException if the login process was interrupted.
     */
    public DefaultEnvironment(Supplier<JDABuilder> builder, NetworkAdapter adapter) throws LoginException, InterruptedException{
        super(XMLConfig.create(new File("config.xml")), adapter);
        
        this.builder = builder;
        
        addShards();
        removeOldGuilds();
        addListeners();
    }
    /**
     * Creates a communicator for every shard.
     * @throws LoginException if the login failed.
     * @throws InterruptedException if the login process was interrupted.
     */
    public final void addShards() throws LoginException, InterruptedException{
        for(int i = 0 ; i < config.getDiscordShards() ; ++i){
            shards.add(new DefaultCommunicator(this, create(i)));
        }
    }
    /**
     * Remove old server files that don't belong to a guild the bot is in anymore.
     */
    public final void removeOldGuilds(){
        File guilds = new File(String.format("%s/guilds",config().getDataFolder()));
        guilds.mkdirs();
        
        //Collect the ids of all guilds
        Set<String> names = guild().stream().map(Guild::getId).collect(Collectors.toSet());
        
        //Remove all files that aren't in the guild space
        Arrays.asList(guilds.list())
                .stream()
                .map(Files::getNameWithoutExtension)
                .filter(n -> !names.contains(n))
                .map(n -> new File(String.format("%s/guilds/%s.server",config.getDataFolder(),n)))
                .forEach(f -> f.delete());
    }
    /**
     * Adds a message listener to each shard.
     */
    private void addListeners(){
        shards.forEach(c -> c.jda().addEventListener(new MessageListener(c)));
    }
    /**
     * Creates a new instance of the JDA that manages the specified shard.
     * @param shard the shard number.
     * @return a JDA instance in that shard.
     * @throws LoginException if the login failed.
     * @throws InterruptedException if the login process was interrupted.
     */
    private JDA create(int shard) throws LoginException, InterruptedException{
        limiter.acquire();
        return builder.get()
                .setStatus(OnlineStatus.ONLINE)
                .setToken(credentials().getDiscordToken())
                .setAutoReconnect(true)
                .useSharding(shard, config().getDiscordShards())
                .build()
                .awaitStatus(JDA.Status.AWAITING_LOGIN_CONFIRMATION);
                
    }
}
