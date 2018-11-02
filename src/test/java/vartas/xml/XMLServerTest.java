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
import java.io.FileWriter;
import java.io.IOException;
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
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.OfflineJDA;

/**
 *
 * @author u/Zavarov
 */
public class XMLServerTest {
    static String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<server>\n" +
"    <entry row=\"reddit\" column=\"subreddit\">\n" +
"        <document>\n" +
"            <entry>1</entry>\n" +
"            <entry>2</entry>\n" +
"        </document>\n" +
"    </entry>\n" +
"    <entry row=\"reddit\" column=\"stuff\">\n" +
"        <document>\n" +
"            <entry>2</entry>\n" +
"            <entry>3</entry>\n" +
"        </document>\n" +
"    </entry>\n" +
"    <entry row=\"filter\" column=\"word\">\n" +
"        <document>\n" +
"            <entry>word</entry>\n" +
"            <entry>text</entry>\n" +
"            <entry>expression</entry>\n" +
"        </document>\n" +
"    </entry>\n" +
"    <entry row=\"role\" column=\"tag\">\n" +
"        <document>\n" +
"            <entry>1</entry>\n" +
"            <entry>2</entry>\n" +
"        </document>\n" +
"    </entry>\n" +
"    <entry row=\"role\" column=\"stuff\">\n" +
"        <document>\n" +
"            <entry>3</entry>\n" +
"        </document>\n" +
"    </entry>\n" +
"    <entry row=\"server\" column=\"prefix\">\n" +
"        <document>\n" +
"            <entry>prefix</entry>\n" +
"        </document>\n" +
"    </entry>\n" +
"</server>";

    @BeforeClass
    public static void create() throws IOException{
        try (FileWriter writer = new FileWriter(new File("src/test/resources/guilds/0.server"))) {
            writer.write(xml);
        }
    }
    XMLServer server;
    GuildImpl guild;
    TextChannelImpl channel;
    RoleImpl role0,role1,role3;
    JDAImpl jda;
    @Before
    public void setUp(){
        server = XMLServer.create(new File("src/test/resources/guilds/0.server"));
        jda = new OfflineJDA();
        guild = new GuildImpl(jda , 1);
        channel = new TextChannelImpl(2, guild);
        guild.getTextChannelsMap().put(channel.getIdLong(), channel);
        role0 = new RoleImpl(0, guild);
        role1 = new RoleImpl(1, guild);
        role3 = new RoleImpl(3, guild);
        guild.getRolesMap().put(role0.getIdLong(), role0);
        guild.getRolesMap().put(role1.getIdLong(), role1);
        guild.getRolesMap().put(role3.getIdLong(), role3);
    }
    
    @Test
    public void containsRedditFeedTest(){
        assertTrue(server.containsRedditFeed("subreddit", new TextChannelImpl(1,guild)));
        assertTrue(server.containsRedditFeed("subreddit", new TextChannelImpl(2,guild)));
        assertFalse(server.containsRedditFeed("subreddit", new TextChannelImpl(3,guild)));
        assertFalse(server.containsRedditFeed("stuff", new TextChannelImpl(1,guild)));
        assertTrue(server.containsRedditFeed("stuff", new TextChannelImpl(2,guild)));
        assertTrue(server.containsRedditFeed("stuff", new TextChannelImpl(3,guild)));
    }
    
    @Test
    public void addRedditFeedTest(){
        assertFalse(server.containsRedditFeed("subreddit", new TextChannelImpl(3,guild)));
        server.addRedditFeed("subreddit", new TextChannelImpl(3,guild));
        assertTrue(server.containsRedditFeed("subreddit", new TextChannelImpl(3,guild)));
        assertFalse(server.containsRedditFeed("junk", new TextChannelImpl(3,guild)));
        assertFalse(server.getRedditFeeds(guild).containsKey("junk"));
        server.addRedditFeed("junk", new TextChannelImpl(3,guild));
        assertTrue(server.containsRedditFeed("junk", new TextChannelImpl(3,guild)));
    }
    
    @Test
    public void removeRedditFeedTest(){
        assertTrue(server.containsRedditFeed("subreddit", new TextChannelImpl(1,guild)));
        assertTrue(server.containsRedditFeed("subreddit", new TextChannelImpl(2,guild)));
        assertTrue(server.containsColumn("subreddit"));
        server.removeRedditFeed("subreddit", new TextChannelImpl(1,guild));
        assertFalse(server.containsRedditFeed("subreddit", new TextChannelImpl(1,guild)));
        server.removeRedditFeed("subreddit", new TextChannelImpl(2,guild));
        assertFalse(server.containsRedditFeed("subreddit", new TextChannelImpl(2,guild)));
        assertFalse(server.containsColumn("subreddit"));
    }
    
    @Test
    public void getRedditFeedsTest(){
        Multimap<String,TextChannel> multimap = server.getRedditFeeds(guild);
        assertEquals(multimap.size(),2);
        assertTrue(multimap.containsEntry("subreddit", channel));
        assertTrue(multimap.containsEntry("stuff", channel));
        assertFalse(server.containsRedditFeed("subreddit", new TextChannelImpl(1,guild)));
        assertTrue(server.containsRedditFeed("subreddit", new TextChannelImpl(2,guild)));
        assertTrue(server.containsRedditFeed("stuff", new TextChannelImpl(2,guild)));
        assertFalse(server.containsRedditFeed("stuff", new TextChannelImpl(3,guild)));
    }
    @Test
    public void getRedditFeedTest(){
        TextChannelImpl channel3 = new TextChannelImpl(3, guild);
        guild.getTextChannelsMap().put(channel3.getIdLong(), channel3);
        assertEquals(server.getRedditFeed(guild, "stuff"),Sets.newHashSet(channel,channel3));
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
        assertFalse(server.isTagged(new RoleImpl(0,guild)));
        assertTrue(server.isTagged(new RoleImpl(1,guild)));
        assertTrue(server.isTagged(new RoleImpl(2,guild)));
        assertTrue(server.isTagged(new RoleImpl(3,guild)));
        assertFalse(server.isTagged(new RoleImpl(4,guild)));
    }
    @Test
    public void tag(){
        assertFalse(server.isTagged(new RoleImpl(4,guild)));
        server.tag("stuff",new RoleImpl(4,guild));
        assertTrue(server.isTagged(new RoleImpl(4,guild)));
    }
    @Test
    public void tagTaggedTest(){
        assertTrue(server.isTagged(new RoleImpl(1,guild)));
        assertEquals(server.getTag(new RoleImpl(1,guild)),"tag");
        server.tag("stuff",new RoleImpl(1,guild));
        assertTrue(server.isTagged(new RoleImpl(1,guild)));
        assertEquals(server.getTag(new RoleImpl(1,guild)),"tag");
    }
    @Test
    public void untagTest(){
        assertTrue(server.isTagged(new RoleImpl(1,guild)));
        server.untag(new RoleImpl(1,guild));
        assertFalse(server.isTagged(new RoleImpl(1,guild)));
    }
    @Test
    public void untagUntaggedTest(){
        RoleImpl role = new RoleImpl(1000,guild);
        assertFalse(server.isTagged(role));
        server.untag(role);
        assertFalse(server.isTagged(role));
    }
    @Test
    public void getTags(){
        Multimap<String,Role> multimap = server.getTags(guild);
        assertEquals(multimap.size(),2);
        assertTrue(multimap.get("tag").contains(new RoleImpl(1,guild)));
        assertTrue(multimap.get("stuff").contains(new RoleImpl(3,guild)));
    }
    @Test
    public void removeNonexistentTest(){
        assertFalse(server.containsRedditFeed("invalid", channel));
        server.removeRedditFeed("invalid", channel);
        assertFalse(server.containsRedditFeed("invalid", channel));
    }
    @Test
    public void containsNonexistentTest(){
        assertFalse(server.containsRedditFeed("invalid", channel));
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
