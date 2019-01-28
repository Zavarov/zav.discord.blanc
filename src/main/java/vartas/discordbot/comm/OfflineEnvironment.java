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

import com.neovisionaries.ws.client.WebSocketFactory;
import java.io.File;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.WebSocketClient;
import net.dv8tion.jda.core.utils.cache.CacheFlag;
import net.dv8tion.jda.core.utils.cache.UpstreamReference;
import okhttp3.OkHttpClient;
import vartas.offlinejraw.OfflineNetworkAdapter;
import vartas.offlinejraw.OfflineRateLimiter;
import vartas.xml.XMLConfig;

/**
 * This class creates an environment that doesn't rely on other clients.<br>
 * Ideal for debugging.
 * @author u/Zavarov
 */
public class OfflineEnvironment extends AbstractEnvironment{
    /**
     * Creates an instance with the configuration file in \"src/test/resources\".
     */
    public OfflineEnvironment(){
        super(XMLConfig.create(new File("src/test/resources/config.xml")), new OfflineNetworkAdapter());
        super.reddit.getClient().setRateLimiter(new OfflineRateLimiter());
        
        for(int i = 0 ; i < config.getDiscordShards() ; ++i){
            shards.add(new OfflineCommunicator(this, create()));
        }
    }
    /**
     * @return a fresh instance of a JDA that isn't aligned to any shard.
     */
    public static JDAImpl create(){
        return new JDAImpl(AccountType.BOT, null, null, new OkHttpClient.Builder().build(), new WebSocketFactory(),
        Executors.newSingleThreadScheduledExecutor(), Executors.newSingleThreadScheduledExecutor(), Executors.newSingleThreadExecutor(),
        false, false, false,
        false, false, false,
        true, true, true,
        0,0,
        new ConcurrentHashMap<>(), EnumSet.of(CacheFlag.GAME)){
            @Override
            public WebSocketClient getClient(){
                client = (client == null) ? new UpstreamReference<>(new WebSocketClient(this, true)) : client;
                return super.getClient();
            }
            @Override
            public JDA awaitStatus(Status status){
                return this;
            }
            @Override
            public RestAction<User> retrieveUserById(String token){
                return retrieveUserById(Long.parseLong(token));
            }
            @Override
            public RestAction<User> retrieveUserById(long token){
                return new RestAction.EmptyRestAction<>(this, this.getUserById(token));
            }
        };
    }
}
