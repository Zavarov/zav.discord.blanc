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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Set;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageType;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.PrivateChannelImpl;
import net.dv8tion.jda.core.entities.impl.ReceivedMessage;
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import net.dv8tion.jda.core.entities.impl.SelfUserImpl;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.discordbot.command.AmbiguousNameException;
import vartas.discordbot.command.UnknownEntityException;
import vartas.discordbot.messages.InteractiveMessage;
import vartas.parser.cfg.ContextFreeGrammar.Builder.Terminal;
import vartas.xml.XMLServer;

/**
 *
 * @author u/Zavarov
 */
public class AbstractCommunicatorTest {
    static OfflineEnvironment environment;
    static OfflineCommunicator comm;
    static GuildImpl guild0;
    static GuildImpl guild1;
    static UserImpl user0;
    static UserImpl user1;
    static MemberImpl member0;
    static MemberImpl member1;
    static MemberImpl memberself;
    static RoleImpl role0;
    static RoleImpl role1;
    static SelfUserImpl self;
    static TextChannelImpl channel0;
    static TextChannelImpl channel1;
    static PrivateChannelImpl channel3;
    static Message message1;
    static Message message2;
    static InteractiveMessage interactive;
    @BeforeClass
    public static void setUp(){
        environment = new OfflineEnvironment();
        comm = (OfflineCommunicator)environment.shards.get(0);
        
        comm.activity.shutdown();
        comm.messages.shutdown();
        
        user0 = new UserImpl(0L,(JDAImpl)comm.jda);
        user1 = new UserImpl(1L,(JDAImpl)comm.jda);
        self = new SelfUserImpl(2L,(JDAImpl)comm.jda);
        
        user0.setName("user0");
        user1.setName("user1");
        self.setName("self");
        
        ((JDAImpl)comm.jda).getUserMap().put(self.getIdLong(),self);
        ((JDAImpl)comm.jda).getUserMap().put(user0.getIdLong(),user0);
        ((JDAImpl)comm.jda).getUserMap().put(user1.getIdLong(),user1);
        
        guild0 = new GuildImpl((JDAImpl)comm.jda,0L);
        guild1 = new GuildImpl((JDAImpl)comm.jda,1L);
        
        guild0.setName("guild0");
        guild1.setName("guild1");
        
        ((JDAImpl)comm.jda).getGuildMap().put(guild0.getIdLong(),guild0);
        ((JDAImpl)comm.jda).getGuildMap().put(guild1.getIdLong(),guild1);
        
        //Custom action for when the interactive message is sent.
        channel0 = new TextChannelImpl(0L,guild0);
        channel1 = new TextChannelImpl(1L,guild0);
        
        channel0.setName("channel0");
        channel1.setName("channel1");
        
        guild0.getTextChannelsMap().put(channel0.getIdLong(),channel0);
        guild0.getTextChannelsMap().put(channel1.getIdLong(),channel1);
        
        role0 = new RoleImpl(0L,guild0);
        role1 = new RoleImpl(1L,guild0);
        
        role0.setName("role0");
        role1.setName("role1");
        
        guild0.getRolesMap().put(role0.getIdLong(),role0);
        guild0.getRolesMap().put(role1.getIdLong(),role1);
        
        memberself = new MemberImpl(guild0,self);
        member0 = new MemberImpl(guild0,user0);
        member1 = new MemberImpl(guild0,user1);
        
        guild0.getMembersMap().put(user0.getIdLong(),member0);
        guild0.getMembersMap().put(user1.getIdLong(),member1);
        guild0.getMembersMap().put(self.getIdLong(),memberself);
        
        
        ((JDAImpl)comm.jda).setSelfUser(self);
        guild0.setPublicRole(role0);
        guild0.setOwner(member0);
        role0.setRawPermissions(Permission.ALL_PERMISSIONS);
        
        channel3 = new PrivateChannelImpl(3L,user1);
        user1.setPrivateChannel(channel3);
        
        message1 = new ReceivedMessage(
                1L, channel1, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "content", "", user1, OffsetDateTime.now()
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
        
        message2 = new ReceivedMessage(
                1L, channel3, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "content", "", user1, OffsetDateTime.now()
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
        
        interactive = new InteractiveMessage.Builder(channel0, user0, comm)
                .addLine("page")
                .build();
    }
    @Before
    public void cleanUp(){
        comm.actions.clear();
        comm.discord.clear();
        comm.servers.clear();
    }
    @Test
    public void jdaTest(){
        assertEquals(comm.jda(),comm.jda);
    }
    @Test
    public void selfTest(){
        assertEquals(comm.self(),comm.jda.getSelfUser());
    }
    @Test
    public void environmentTest(){
        assertEquals(comm.environment(),comm.environment);
    }
    @Test
    public void submitTest(){
        comm.submit(() -> comm.actions.add("submitted"));
        comm.executor.shutdown();
        while(!comm.executor.isTerminated()){}
        assertEquals(comm.actions,Arrays.asList("submitted"));
        setUp();
    }
    @Test
    public void shutdownTest(){
        comm.shutdown();
        assertTrue(comm.executor.isShutdown());
        setUp();
    }
    @Test
    public void guildCollectionTest(){
        assertEquals(comm.guild().size(),2);
        assertTrue(comm.guild().containsAll(Arrays.asList(guild0, guild1)));
    }
    @Test
    public void serverTest(){
        XMLServer server = comm.server(guild0);
        assertEquals(server.getPrefix(),"prefix");
    }
    @Test
    public void serverNewTest(){
        XMLServer server = comm.server(new GuildImpl(null, 1L));
        assertTrue(server.isEmpty());
    }
    @Test
    public void serverMemoryTest(){
        XMLServer server = comm.server(guild0);
        assertEquals(server.getPrefix(),"prefix");
        
        assertTrue(comm.servers.containsKey(guild0));
        
        server = comm.server(guild0);
        assertEquals(server.getPrefix(),"prefix");
    }
    @Test
    public void activityTest(){
        comm.activity(channel0);
        comm.activity(channel1);
        //Finalize the most recent update
        comm.activity.run();
        
        TimeSeriesCollection series = comm.activity.createChannelSeries(guild0, Lists.newArrayList(channel0));
        assertEquals(series.getSeriesCount(),2);
        //Guild 0
        TimeSeries entry = series.getSeries(0);
        assertEquals(entry.getItemCount(),1);
        assertEquals(entry.getValue(0).doubleValue(),2.0/comm.environment.config().getActivityInterval(),0.001);
        
        //Channel 0
        entry = series.getSeries(1);
        assertEquals(entry.getItemCount(),1);
        assertEquals(entry.getValue(0).doubleValue(),1.0/comm.environment.config().getActivityInterval(),0.001);
    }
    @Test
    public void activityChartTest(){
        comm.activity.run();
        
        JFreeChart chart = comm.activity(guild0, Lists.newArrayList());
        
        assertEquals(chart.getXYPlot().getDatasetCount(),2);
        assertEquals(chart.getXYPlot().getDataset(0).getSeriesCount(),2);
        assertEquals(chart.getXYPlot().getDataset(1).getSeriesCount(),1);
    }
    @Test
    public void defaultUserTest(){
        Set<User> user = comm.defaultUser(Arrays.asList(new Terminal(user0.getId(),"integer")),message1);
        assertEquals(user,Sets.newHashSet(user0));
    }
    @Test
    public void defaultUserEmptyTest(){
        Set<User> user = comm.defaultUser(Arrays.asList(),message1);
        assertEquals(user,Sets.newHashSet(user1));
    }
    @Test
    public void userTest(){
        Set<User> user = comm.user(Arrays.asList(new Terminal(user0.getId(),"integer")),message1);
        assertEquals(user,Sets.newHashSet(user0));
    }
    @Test
    public void userNameTest(){
        Set<User> user = comm.user(Arrays.asList(new Terminal(user0.getName(),"quotation")),message1);
        assertEquals(user,Sets.newHashSet(user0));
    }
    @Test
    public void roleNameTest(){
        Set<Role> role = comm.role(Arrays.asList(new Terminal(role0.getName(),"quotation")),message1);
        assertEquals(role,Sets.newHashSet(role0));
    }
    @Test
    public void roleIdTest(){
        Set<Role> role = comm.role(Arrays.asList(new Terminal(role0.getId(),"integer")),message1);
        assertEquals(role,Sets.newHashSet(role0));
    }
    @Test
    public void defaultMemberTest(){
        Set<Member> member = comm.defaultMember(Arrays.asList(new Terminal(user0.getId(),"integer")),message1);
        assertEquals(member,Sets.newHashSet(member0));
    }
    @Test
    public void defaultMemberEmpty(){
        Set<Member> member = comm.defaultMember(Arrays.asList(),message1);
        assertEquals(member,Sets.newHashSet(member1));
    }
    @Test
    public void defaultMemberPrivateTest(){
        Set<Member> member = comm.defaultMember(Arrays.asList(new Terminal(user0.getId(),"integer")),message2);
        assertTrue(member.isEmpty());
    }
    @Test
    public void defaultMemberNameTest(){
        Set<Member> member = comm.defaultMember(Arrays.asList(new Terminal(user0.getName(),"quotation")),message1);
        assertEquals(member,Sets.newHashSet(member0));
    }
    @Test
    public void memberTest(){
        Set<Member> member = comm.member(Arrays.asList(new Terminal(user0.getId(),"integer")),message1);
        assertEquals(member,Sets.newHashSet(member0));
    }
    @Test
    public void memberEmptyTest(){
        Set<Member> member = comm.member(Arrays.asList(),message1);
        assertTrue(member.isEmpty());
    }
    @Test
    public void defaultTextChannelTest(){
        Set<TextChannel> channel = comm.defaultTextChannel(Arrays.asList(new Terminal(channel0.getId(),"integer")),message1);
        assertEquals(channel,Sets.newHashSet(channel0));
    }
    @Test
    public void defaultTextChannelEmptyTest(){
        Set<TextChannel> channel = comm.defaultTextChannel(Arrays.asList(),message1);
        assertEquals(channel,Sets.newHashSet(channel1));
    }
    @Test
    public void defaultTextChannelPrivateTest(){
        Set<TextChannel> channel = comm.defaultTextChannel(Arrays.asList(new Terminal(channel0.getId(),"integer")),message2);
        assertTrue(channel.isEmpty());
    }
    @Test
    public void textChannelNameTest(){
        Set<TextChannel> channel = comm.textChannel(Arrays.asList(new Terminal(channel0.getName(),"quotation")),message1);
        assertEquals(channel,Sets.newHashSet(channel0));
    }
    
    @Test
    public void textChannelIdTest(){
        Set<TextChannel> channel = comm.textChannel(Arrays.asList(new Terminal(channel0.getId(),"integer")),message1);
        assertEquals(channel,Sets.newHashSet(channel0));
    }
    @Test
    public void defaultGuildTest(){
        Set<Guild> guild = comm.defaultGuild(Arrays.asList(new Terminal(guild1.getId(),"integer")),message1);
        assertEquals(guild,Sets.newHashSet(guild1));
    }
    @Test
    public void defaultGuildNameTest(){
        Set<Guild> guild = comm.defaultGuild(Arrays.asList(new Terminal(guild1.getName(),"quotation")),message1);
        assertEquals(guild,Sets.newHashSet(guild1));
    }
    @Test
    public void defaultGuildEmptyTest(){
        Set<Guild> guild = comm.defaultGuild(Arrays.asList(),message1);
        assertEquals(guild,Sets.newHashSet(guild0));
    }
    @Test
    public void defaultGuildPrivateTest(){
        Set<Guild> guild = comm.defaultGuild(Arrays.asList(new Terminal("2","integer")),message2);
        assertTrue(guild.isEmpty());
    }
    @Test
    public void guildTest(){
        Set<Guild> guild = comm.guild(Arrays.asList(new Terminal(guild1.getId(),"integer")),message1);
        assertEquals(guild,Sets.newHashSet(guild1));
    }
    @Test
    public void sendTest(){
        comm.send(interactive);
        assertEquals(comm.actions,Arrays.asList("action queued"));
    }
    @Test
    public void presenceTest(){
        assertEquals(comm.presence(),comm.jda.getPresence());
    }
    @Test(expected=UnknownEntityException.class)
    public void getEntityUnknownIntegerTest(){
        comm.guild(Arrays.asList(new Terminal("2","integer")),message1);
    }
    @Test(expected=UnknownEntityException.class)
    public void getEntityUnknownStringTest(){
        comm.user(Arrays.asList(new Terminal("user3","quotation")),message1);
    }
    @Test(expected=AmbiguousNameException.class)
    public void getEntityAmbiguousStringTest(){
        user1.setName(user0.getName());
        comm.user(Arrays.asList(new Terminal(user1.getName(),"quotation")),message1);
    }
}
