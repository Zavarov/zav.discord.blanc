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
package vartas.discordbot.threads;

import com.google.common.collect.Lists;
import java.util.Date;
import java.util.Locale;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.SelfUserImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.discordbot.comm.Communicator;
import vartas.discordbot.comm.OfflineCommunicator;
import vartas.discordbot.comm.OfflineEnvironment;
import vartas.discordbot.threads.ActivityTracker.Data;
import vartas.discordbot.threads.ActivityTracker.Dataset;
import static vartas.discordbot.threads.ActivityTracker.UTC;

/**
 *
 * @author u/Zavarov
 */
public class ActivityTrackerTest {
    static Communicator comm;
    static GuildImpl guild0;
    static TextChannelImpl channel1;
    static TextChannelImpl channel2;
    static SelfUserImpl self;
    static UserImpl user0;
    static UserImpl user1;
    static UserImpl user2;
    static MemberImpl member0;
    static MemberImpl member1;
    static MemberImpl member2;
    static MemberImpl memberself;
    @BeforeClass
    public static void create(){
        comm = new OfflineCommunicator(new OfflineEnvironment(), OfflineEnvironment.create());
        
        guild0 = new GuildImpl((JDAImpl)comm.jda(),0L);
        guild0.setName("guild0");
        ((JDAImpl)comm.jda()).getGuildMap().put(guild0.getIdLong(),guild0);
        
        channel1 = new TextChannelImpl(1L,guild0);
        channel2 = new TextChannelImpl(2L,guild0);
        channel1.setName("channel0");
        channel2.setName("channel1");
        guild0.getTextChannelsMap().put(channel1.getIdLong(),channel1);
        guild0.getTextChannelsMap().put(channel2.getIdLong(),channel2);
        
        user0 = new UserImpl(0L,(JDAImpl)comm.jda());
        user1 = new UserImpl(1L,(JDAImpl)comm.jda());
        user2 = new UserImpl(2L,(JDAImpl)comm.jda());
        self = new SelfUserImpl(3L,(JDAImpl)comm.jda());
        
        ((JDAImpl)comm.jda()).getUserMap().put(self.getIdLong(),self);
        ((JDAImpl)comm.jda()).getUserMap().put(user0.getIdLong(),user0);
        ((JDAImpl)comm.jda()).getUserMap().put(user1.getIdLong(),user1);
        ((JDAImpl)comm.jda()).getUserMap().put(user2.getIdLong(),user2);
        
        memberself = new MemberImpl(guild0,self);
        member0 = new MemberImpl(guild0,user0);
        member1 = new MemberImpl(guild0,user1);
        member2 = new MemberImpl(guild0,user2);
        
        guild0.getMembersMap().put(user0.getIdLong(),member0);
        guild0.getMembersMap().put(user1.getIdLong(),member1);
        guild0.getMembersMap().put(user2.getIdLong(),member2);
        guild0.getMembersMap().put(self.getIdLong(),memberself);
    }
    ActivityTracker tracker;
    @Before
    public void setUp(){
        user0.setBot(false);
        user1.setBot(false);
        user2.setBot(false);
        self.setBot(false);
        
        member0.setOnlineStatus(OnlineStatus.ONLINE);
        member1.setOnlineStatus(OnlineStatus.ONLINE);
        member2.setOnlineStatus(OnlineStatus.ONLINE);
        memberself.setOnlineStatus(OnlineStatus.ONLINE);
        tracker = new ActivityTracker(comm);
    }
    @Test
    public void shutdownTest(){
        assertFalse(tracker.executor.isShutdown());
        tracker.shutdown();
        assertTrue(tracker.executor.isShutdown());
    }
    @Test
    public void measureTest(){
        Dataset dataset = tracker.measure();
        assertEquals(dataset.size(),1);
        assertTrue(dataset.containsKey(guild0));
        
        Data data = dataset.get(guild0);
        assertEquals(data.all_member,4);
        assertEquals(data.member_online,4);
    }
    @Test
    public void measureBotTest(){
        user0.setBot(true);
        
        Dataset dataset = tracker.measure();
        assertEquals(dataset.size(),1);
        assertTrue(dataset.containsKey(guild0));
        
        Data data = dataset.get(guild0);
        assertEquals(data.all_member,3);
        assertEquals(data.member_online,3);
    }
    @Test
    public void measureOfflineTest(){
        member0.setOnlineStatus(OnlineStatus.OFFLINE);
        
        Dataset dataset = tracker.measure();
        assertEquals(dataset.size(),1);
        assertTrue(dataset.containsKey(guild0));
        
        Data data = dataset.get(guild0);
        assertEquals(data.all_member,4);
        assertEquals(data.member_online,3);
    }
    @Test
    public void updateTest(){
        tracker.executor.shutdownNow();
        while(!tracker.executor.isTerminated()){}
        
        Dataset dataset = new Dataset();
        assertFalse(tracker.queue.contains(dataset));
        tracker.update(dataset);
        assertTrue(tracker.queue.contains(dataset));
        assertEquals(tracker.queue.tail(),dataset);

    }
    @Test
    public void increaseTest(){
        tracker.executor.shutdownNow();
        while(!tracker.executor.isTerminated()){}
        
        assertFalse(tracker.queue.tail().get(guild0).posts.containsKey(channel1));
        tracker.increase(channel1);
        assertTrue(tracker.queue.tail().get(guild0).posts.containsKey(channel1));
        assertEquals(tracker.queue.tail().get(guild0).posts.get(channel1).longValue(),1L);
        tracker.increase(channel1);
        assertEquals(tracker.queue.tail().get(guild0).posts.get(channel1).longValue(),2L);
    }
    @Test
    public void runTest(){
        tracker.executor.shutdownNow();
        while(!tracker.executor.isTerminated()){}
        
        assertEquals(tracker.queue.size(),1);
        tracker.run();
        assertEquals(tracker.queue.size(),2);
    }
    
    @Test
    public void getChannelSeriesTest() throws InterruptedException{
        tracker.executor.shutdownNow();
        while(!tracker.executor.isTerminated()){}
        
        tracker.queue.tail().get(guild0).time = new Minute(new Date(0), UTC, Locale.ENGLISH);
        
        tracker.increase(channel1);
        tracker.run();
        tracker.increase(channel1);
        tracker.increase(channel2);
        tracker.increase(channel2);
        tracker.run();
        //Finish things up
        TimeSeriesCollection series = tracker.createChannelSeries(guild0, Lists.newArrayList(channel2));
        assertEquals(series.getSeriesCount(),2);
        TimeSeries entry = series.getSeries(0);
        assertEquals(entry.getItemCount(),2);
        assertEquals(entry.getValue(0),1.0/comm.environment().config().getActivityInterval());
        assertEquals(entry.getValue(1),3.0/comm.environment().config().getActivityInterval());
        
        entry = series.getSeries(1);
        assertEquals(entry.getItemCount(),2);
        assertEquals(entry.getValue(0),0.0/comm.environment().config().getActivityInterval());
        assertEquals(entry.getValue(1),2.0/comm.environment().config().getActivityInterval());
    }
    
    @Test
    public void getMemberSeriesTest() throws InterruptedException{
        tracker.executor.shutdownNow();
        while(!tracker.executor.isTerminated()){}
        
        tracker.queue.tail().get(guild0).time = new Minute(new Date(0), UTC, Locale.ENGLISH);
        
        user0.setBot(true);
        member1.setOnlineStatus(OnlineStatus.OFFLINE);
        
        tracker.run();
        tracker.run();
        //Finish things up
        TimeSeriesCollection series = tracker.createMemberSeries(guild0);
        //Members online
        assertEquals(series.getSeriesCount(),2);
        TimeSeries entry = series.getSeries(0);
        assertEquals(entry.getItemCount(),2);
        assertEquals(entry.getValue(0),4.0);
        assertEquals(entry.getValue(1),2.0);
        
        //All members
        entry = series.getSeries(1);
        assertEquals(entry.getItemCount(),2);
        assertEquals(entry.getValue(0),4.0);
        assertEquals(entry.getValue(1),3.0);
    }
    
    @Test
    public void createChartTest() throws InterruptedException{
        tracker.executor.shutdownNow();
        while(!tracker.executor.isTerminated()){}
        
        tracker.queue.tail().get(guild0).time = new Minute(new Date(0), UTC, Locale.ENGLISH);
        tracker.run();
        
        JFreeChart chart = tracker.createChart(guild0, Lists.newArrayList());
        
        assertEquals(chart.getXYPlot().getDatasetCount(),2);
        assertEquals(chart.getXYPlot().getDataset(0).getSeriesCount(),2);
        assertEquals(chart.getXYPlot().getDataset(1).getSeriesCount(),1);
    }
}