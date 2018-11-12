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

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.security.auth.login.LoginException;
import net.dean.jraw.http.NetworkAdapter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.offlinejraw.OfflineNetworkAdapter;

/**
 *
 * @author u/Zavarov
 */
public class DefaultEnvironmentTest {
    Supplier<JDABuilder> builder = () -> new JDABuilder(AccountType.BOT){
        @Override
        public JDA build(){
            return OfflineEnvironment.create();
        }
    };
    NetworkAdapter adapter = new OfflineNetworkAdapter();
    DefaultEnvironment environment;
    static double rate;
    @BeforeClass
    public static void startUp(){
        rate = DefaultEnvironment.limiter.getRate();
        DefaultEnvironment.limiter.setRate(1000);
    }
    @AfterClass
    public static void tearDown(){
        DefaultEnvironment.limiter.setRate(rate);
    }
    @Before
    public void setUp() throws LoginException, InterruptedException{
        environment = new DefaultEnvironment(builder,adapter);
    }
    @Test
    public void addShardsTest() throws LoginException, InterruptedException{
        environment.shards.clear();
        
        environment.addShards();
        
        assertEquals(environment.shards.size(), environment.config.getDiscordShards());
    }
    @Test
    public void removeOldGuildTest() throws IOException{
        File file = new File("data/guilds/0.server");
        file.createNewFile();
        
        assertTrue(file.exists());
        environment.removeOldGuilds();
        assertFalse(file.exists());
    }
    @Test
    public void removeOldGuildNoneTest() throws IOException{
        File file = new File("data/guilds/0.server");
        file.createNewFile();
        
        JDAImpl jda = (JDAImpl)environment.comm(0).jda();
        jda.getGuildMap().put(0, new GuildImpl(jda,0));
        
        assertTrue(file.exists());
        environment.removeOldGuilds();
        assertTrue(file.exists());
    }
    @Test
    public void addGuildsTest(){
        JDAImpl jda = (JDAImpl)environment.comm(0).jda();
        GuildImpl guild = new GuildImpl(jda,0);
        TextChannelImpl channel = new TextChannelImpl(1,guild);
        jda.getGuildMap().put(guild.getIdLong(), guild);
        guild.getTextChannelsMap().put(channel.getIdLong(), channel);
        
        environment.comm(guild).server(guild).addRedditFeed("subreddit", channel);
        
        assertFalse(environment.feed.containsFeed("subreddit", channel));
        environment.addRedditFeeds();
        assertTrue(environment.feed.containsFeed("subreddit", channel));
        
    }
}
