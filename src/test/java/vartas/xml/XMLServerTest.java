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
package vartas.xml;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.io.File;
import java.util.Set;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.discordbot.comm.OfflineEnvironment;

/**
 *
 * @author u/Zavarov
 */
public class XMLServerTest {
    XMLServer server;
    GuildImpl guild;
    TextChannelImpl channel1;
    TextChannelImpl channel2;
    TextChannelImpl channel3;
    RoleImpl role0,role1,role2,role3,role4;
    JDAImpl jda;
    @Before
    public void setUp(){
        server = XMLServer.create(new File("src/test/resources/guilds/0.server"));
        
        jda = OfflineEnvironment.create();
        guild = new GuildImpl(jda , 1);
        channel1 = new TextChannelImpl(1, guild);
        channel2 = new TextChannelImpl(2, guild);
        channel3 = new TextChannelImpl(3, guild);
        guild.getTextChannelsMap().put(channel2.getIdLong(), channel2);
        role0 = new RoleImpl(0, guild);
        role1 = new RoleImpl(1, guild);
        role2 = new RoleImpl(2, guild);
        role3 = new RoleImpl(3, guild);
        role4 = new RoleImpl(4, guild);
        guild.getRolesMap().put(role0.getIdLong(), role0);
        guild.getRolesMap().put(role1.getIdLong(), role1);
        guild.getRolesMap().put(role3.getIdLong(), role3);
    }
    
    @Test
    public void containsRedditFeedTest(){
        assertTrue(server.containsRedditFeed("subreddit", channel1));
        assertTrue(server.containsRedditFeed("subreddit", channel2));
        assertFalse(server.containsRedditFeed("subreddit", channel3));
        assertFalse(server.containsRedditFeed("stuff", channel1));
        assertTrue(server.containsRedditFeed("stuff", channel2));
        assertTrue(server.containsRedditFeed("stuff", channel3));
    }
    
    @Test
    public void addRedditFeedTest(){
        assertFalse(server.containsRedditFeed("subreddit", channel3));
        server.addRedditFeed("subreddit", channel3);
        assertTrue(server.containsRedditFeed("subreddit", channel3));
        
        assertFalse(server.containsRedditFeed("junk", channel3));
        assertFalse(server.getRedditFeeds(guild).containsKey("junk"));
        server.addRedditFeed("junk", channel3);
        assertTrue(server.containsRedditFeed("junk", channel3));
    }
    
    @Test
    public void removeRedditFeedTest(){
        assertTrue(server.containsRedditFeed("subreddit", channel1));
        assertTrue(server.containsRedditFeed("subreddit", channel2));
        assertTrue(server.containsColumn("subreddit"));
        server.removeRedditFeed("subreddit", channel1);
        assertFalse(server.containsRedditFeed("subreddit", channel1));
        server.removeRedditFeed("subreddit", channel2);
        assertFalse(server.containsRedditFeed("subreddit", channel2));
        assertFalse(server.containsColumn("subreddit"));
    }
    
    @Test
    public void getRedditFeedsTest(){
        Multimap<String,TextChannel> multimap = server.getRedditFeeds(guild);
        assertEquals(multimap.entries().size(),2);
        assertTrue(multimap.containsEntry("subreddit", channel2));
        assertTrue(multimap.containsEntry("stuff", channel2));
        assertFalse(server.containsRedditFeed("subreddit", channel1));
        assertTrue(server.containsRedditFeed("subreddit", channel2));
        assertTrue(server.containsRedditFeed("stuff", channel2));
        assertFalse(server.containsRedditFeed("stuff", channel3));
    }
    @Test
    public void getRedditFeedTest(){
        guild.getTextChannelsMap().put(channel3.getIdLong(), channel3);
        assertEquals(server.getRedditFeed(guild, "stuff"),Sets.newHashSet(channel2,channel3));
    }
    @Test
    public void isFilteredTest(){
        assertFalse(server.isFiltered("stuff"));
        assertTrue(server.isFiltered("text"));
        assertTrue(server.isFiltered("word"));
        assertTrue(server.isFiltered("expression"));
    }
    @Test
    public void addFilterTest(){
        assertFalse(server.isFiltered("stuff"));
        server.addFilter("stuff");
        assertTrue(server.isFiltered("stuff"));
        assertTrue(server.isFiltered("text"));
        assertTrue(server.isFiltered("word"));
        assertTrue(server.isFiltered("expression"));
    }
    @Test
    public void removeFilterTest(){
        assertTrue(server.isFiltered("text"));
        server.removeFilter("text");
        assertFalse(server.isFiltered("text"));
        assertTrue(server.isFiltered("word"));
        assertTrue(server.isFiltered("expression"));
    }
    @Test
    public void getFilterTest(){
        Set<String> filters = server.getFilter();
        assertEquals(filters.size(),3);
        assertTrue(filters.contains("text"));
        assertTrue(filters.contains("word"));
        assertTrue(filters.contains("expression"));
        assertTrue(server.isFiltered("text"));
        assertTrue(server.isFiltered("word"));
        assertTrue(server.isFiltered("expression"));
    }
    @Test
    public void isTaggedTest(){
        assertFalse(server.isTagged(role0));
        assertTrue(server.isTagged(role1));
        assertTrue(server.isTagged(role2));
        assertTrue(server.isTagged(role3));
        assertFalse(server.isTagged(role4));
    }
    @Test
    public void tag(){
        assertFalse(server.isTagged(role4));
        server.tag("stuff",role4);
        assertTrue(server.isTagged(role4));
    }
    @Test
    public void tagTaggedTest(){
        assertTrue(server.isTagged(role1));
        assertEquals(server.getTag(role1),"tag");
        server.tag("stuff",role1);
        assertTrue(server.isTagged(role1));
        assertEquals(server.getTag(role1),"tag");
    }
    @Test
    public void untagTest(){
        assertTrue(server.isTagged(role1));
        server.untag(role1);
        assertFalse(server.isTagged(role1));
    }
    @Test
    public void untagUntaggedTest(){
        assertFalse(server.isTagged(role4));
        server.untag(role4);
        assertFalse(server.isTagged(role4));
    }
    @Test
    public void getTags(){
        Multimap<String,Role> multimap = server.getTags(guild);
        assertEquals(multimap.entries().size(),2);
        assertTrue(multimap.get("tag").contains(role1));
        assertTrue(multimap.get("stuff").contains(role3));
    }
    @Test
    public void removeNonexistentTest(){
        assertFalse(server.containsRedditFeed("invalid", channel2));
        server.removeRedditFeed("invalid", channel2);
        assertFalse(server.containsRedditFeed("invalid", channel2));
    }
    @Test
    public void containsNonexistentTest(){
        assertFalse(server.containsRedditFeed("invalid", channel2));
    }
    @Test
    public void getSetNonexistentTest(){
        assertTrue(server.getRedditFeed(guild, "invalid").isEmpty());
    }
    @Test
    public void getPrefixTest(){
        assertEquals(server.getPrefix(),"prefix");
    }
    @Test
    public void getPrefixNullTest(){
        server.remove("server", "prefix");
        assertNull(server.getPrefix());
    }
    @Test
    public void setPrefixTest(){
        assertEquals(server.get("server","prefix"), Lists.newArrayList("prefix"));
        server.setPrefix("junk");
        assertEquals(server.get("server","prefix"), Lists.newArrayList("junk"));
    }
    @Test
    public void removePrefixTest(){
        assertEquals(server.get("server","prefix"), Lists.newArrayList("prefix"));
        server.setPrefix(null);
        assertFalse(server.contains("server","prefix"));
    }
    @Test
    public void hasPrefixTest(){
        assertTrue(server.hasPrefix());
        server.remove("server", "prefix");
        assertFalse(server.hasPrefix());
    }
    @Test
    public void getMultimapEmptySetTest(){
        guild.getRolesMap().clear();
        assertTrue(server.getTags(guild).isEmpty());
        assertFalse(server.containsRow("role"));
    }
}
