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
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.OfflineInstance;
import vartas.discordbot.threads.ActivityTracker.Data;
import vartas.discordbot.threads.ActivityTracker.Dataset;
import static vartas.discordbot.threads.ActivityTracker.UTC;

/**
 *
 * @author u/Zavarov
 */
public class ActivityTrackerTest {
    OfflineInstance instance;
    ActivityTracker tracker;
    @Before
    public void setUp(){
        instance = new OfflineInstance();
        tracker = new ActivityTracker(instance.jda,1);
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
        assertTrue(dataset.containsKey(instance.guild));
        
        Data data = dataset.get(instance.guild);
        assertEquals(data.all_member,4);
        assertEquals(data.member_online,4);
    }
    @Test
    public void measureBotTest(){
        instance.user.setBot(true);
        
        Dataset dataset = tracker.measure();
        assertEquals(dataset.size(),1);
        assertTrue(dataset.containsKey(instance.guild));
        
        Data data = dataset.get(instance.guild);
        assertEquals(data.all_member,3);
        assertEquals(data.member_online,3);
    }
    @Test
    public void measureOfflineTest(){
        instance.member.setOnlineStatus(OnlineStatus.OFFLINE);
        
        Dataset dataset = tracker.measure();
        assertEquals(dataset.size(),1);
        assertTrue(dataset.containsKey(instance.guild));
        
        Data data = dataset.get(instance.guild);
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
        
        assertFalse(tracker.queue.tail().get(instance.guild).posts.containsKey(instance.channel1));
        tracker.increase(instance.guild, instance.channel1);
        assertTrue(tracker.queue.tail().get(instance.guild).posts.containsKey(instance.channel1));
        assertEquals(tracker.queue.tail().get(instance.guild).posts.get(instance.channel1).longValue(),1L);
        tracker.increase(instance.guild, instance.channel1);
        assertEquals(tracker.queue.tail().get(instance.guild).posts.get(instance.channel1).longValue(),2L);
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
        
        tracker.queue.tail().get(instance.guild).time = new Minute(new Date(0), UTC, Locale.ENGLISH);
        
        tracker.increase(instance.guild, instance.channel1);
        tracker.run();
        tracker.increase(instance.guild, instance.channel1);
        tracker.increase(instance.guild, instance.channel2);
        tracker.increase(instance.guild, instance.channel2);
        tracker.run();
        //Finish things up
        TimeSeriesCollection series = tracker.createChannelSeries(instance.guild, Lists.newArrayList(instance.channel2));
        assertEquals(series.getSeriesCount(),2);
        TimeSeries entry = series.getSeries(0);
        assertEquals(entry.getItemCount(),2);
        assertEquals(entry.getValue(0),1.0);
        assertEquals(entry.getValue(1),3.0);
        
        entry = series.getSeries(1);
        assertEquals(entry.getItemCount(),2);
        assertEquals(entry.getValue(0),0.0);
        assertEquals(entry.getValue(1),2.0);
    }
    
    @Test
    public void getMemberSeriesTest() throws InterruptedException{
        tracker.executor.shutdownNow();
        while(!tracker.executor.isTerminated()){}
        
        tracker.queue.tail().get(instance.guild).time = new Minute(new Date(0), UTC, Locale.ENGLISH);
        
        instance.user.setBot(true);
        instance.self_member.setOnlineStatus(OnlineStatus.OFFLINE);
        tracker.run();
        tracker.run();
        //Finish things up
        TimeSeriesCollection series = tracker.createMemberSeries(instance.guild);
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
        
        tracker.queue.tail().get(instance.guild).time = new Minute(new Date(0), UTC, Locale.ENGLISH);
        tracker.run();
        
        JFreeChart chart = tracker.createChart(instance.guild, Lists.newArrayList());
        
        assertEquals(chart.getXYPlot().getDatasetCount(),2);
        assertEquals(chart.getXYPlot().getDataset(0).getSeriesCount(),2);
        assertEquals(chart.getXYPlot().getDataset(1).getSeriesCount(),1);
    }
}