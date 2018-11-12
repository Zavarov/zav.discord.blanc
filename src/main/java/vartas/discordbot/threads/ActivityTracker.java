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

package vartas.discordbot.threads;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MINUTES;
import java.util.function.Function;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.JDALogger;
import org.atteo.evo.inflector.English;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.slf4j.Logger;
import vartas.discordbot.comm.Communicator;

/**
 * This class keeps track of the activity in all guilds in the respective shard.
 * @author u/Zavarov
 */
public class ActivityTracker implements Runnable, Killable{
    /**
     * The executor that updates the tracker in a fixed interval
     */
    protected final ScheduledExecutorService executor;
    /**
     * The log for this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The communicator of the program.
     */
    protected final Communicator comm;
    /**
     * A queue storing all data points that also removes the ones that are too old.
     */
    protected final EvictingLinkedQueue<Dataset> queue;
    /**
     * A mutex to assure that only one thread can access the queue at a time.
     */
    protected Semaphore access = new Semaphore(1);
    /**
     * Greenwich mean time.
     */
    protected final static TimeZone UTC = TimeZone.getTimeZone("UTC");
    /**
     * @param comm the communicator of the program.
     */
    public ActivityTracker(Communicator comm){
        this.comm = comm;
        
        queue = new EvictingLinkedQueue<>(comm.environment().config().getActivityInterval());
        queue.add(measure());
        executor = Executors.newSingleThreadScheduledExecutor(
        new ThreadFactoryBuilder().setNameFormat("Activity Executor").build());
        executor.scheduleAtFixedRate(
                ActivityTracker.this, 
                comm.environment().config().getActivityInterval(),  
                comm.environment().config().getActivityInterval(), 
                TimeUnit.MINUTES);
        
        log.info("Activity Tracker started.");
    }
    /**
     * Creates an image based on a set of data of the member and channel in this guild.
     * @param guild the guild to get the name.
     * @param textchannels all channels whose data also has to be plotted.
     * @return the image representing the data.
     */
    public JFreeChart createChart(Guild guild, Collection<TextChannel> textchannels){
        TimeSeriesCollection members = createMemberSeries(guild);
        TimeSeriesCollection channels = createChannelSeries(guild, textchannels);
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(guild.getName(), "Time in UTC", null, null, true, false, false);
        XYPlot plot = chart.getXYPlot();
        plot.setRangeAxis(0, new NumberAxis("Members"));
        plot.setRangeAxis(1, new NumberAxis("#posts / min"));
        //Normalize the values
        plot.getRangeAxis().setLowerBound(0);
        
        plot.setDataset(0, members);
        plot.setDataset(1, channels);
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);
        
        return chart;
    }
    /**
     * Creates also generates the series for the amount of active members
     * and the total amount of members in the server.
     * @param guild the guild in which the bot is in.
     * @return a series of data points, showing the change in the past day.
     */
    public TimeSeriesCollection createMemberSeries(Guild guild){
        TimeSeriesCollection collection = new TimeSeriesCollection();
        collection.addSeries(create("Members online",guild,k -> new Double(k.member_online)));
        collection.addSeries(create("All members",guild,k -> new Double(k.all_member)));
        return collection;
    }
    /**
     * Creates a dataset that shows the total activity in all channels and also in explicit specified channels.
     * and the total amount of members in the server.
     * @param guild the guild in which the bot is in.
     * @param channels all channels whose data also has to be plotted.
     * @return a series of data points, showing the change in the past day.
     */
    public TimeSeriesCollection createChannelSeries(Guild guild, Collection<TextChannel> channels){
        TimeSeriesCollection collection = new TimeSeriesCollection();
        collection.addSeries(create("All channels", 
                guild, 
                k -> k.posts.values().stream().mapToLong(Long::longValue).sum()*1.0/comm.environment().config().getActivityInterval()));
        
        channels.forEach(c -> collection.addSeries(create(
                "#"+c.getName(),
                guild,
                k -> k.posts.computeIfAbsent(c, j -> (long)0)*1.0/comm.environment().config().getActivityInterval()))
        );
        return collection;
    }
    /**
     * A more general implementation that creates a dataset for an unspecified entry type.
     * @param title the name of the dataset.
     * @param guild the guild this set is in.
     * @param entry a function that transforms the entry into a value.
     * @return the created dataset.
     * @throws InterruptedException if the program was interrupted before it could finish.
     */
    private TimeSeries create(String title, Guild guild, Entry entry){
        TimeSeries series = new TimeSeries(title);
        access.acquireUninterruptibly();
        List<Dataset> list = new ObjectArrayList<>(queue);
        access.release();
        //The counter for the newest entries is still being increased
        for(int i = 0 ; i < list.size() - 1 ; ++i){
            list.get(i).computeIfPresent(guild, (k,v) -> { series.add(v.time,entry.apply(v)); return v;});
        }
        return series;
    }
    /**
     * Increases the counter for the messages in the specified channel.
     * @param channel the channel the message was sent int.
     */
    public synchronized void increase(TextChannel channel){
        //The bot joined a new guild since the last update.
        queue.tail().computeIfAbsent(channel.getGuild(), (g) -> measure(g))
                .posts
                .compute(channel, (k,v) -> v == null ? 1L : ++v);
    }
    /**
     * Measures the current time, the total number of people and the number of
     * people that are online for each guild.
     * @return a dataset of the currently measured values for each guild.
     */
    protected final Dataset measure(){
        Dataset dataset = new Dataset();
        comm.guild().forEach(guild -> dataset.put(guild,measure(guild)));
        return dataset;
    }
    /**
     * Measures the data for the specified guild.
     * @param guild the guild that is measured.
     * @return the data sample of the guild.
     */
    protected final Data measure(Guild guild){
        Minute now = new Minute(new Date(),UTC,Locale.ENGLISH);
        long total = guild.getMembers()
                .stream().
                filter(m -> !m.getUser().isBot())
                .count();
        long online = guild.getMembers()
                .stream()
                .filter(m -> m.getOnlineStatus() != OnlineStatus.OFFLINE)
                .filter(m -> !m.getUser().isBot())
                .count();
        return new Data(now,total,online);
    }
    /**
     * Adds a new dataset to the queue and dismissed the oldest one if the queue
     * is full.
     * @param dataset the new dataset. 
     */
    protected void update(Dataset dataset){
        access.acquireUninterruptibly();
        queue.add(dataset);
        access.release();
        log.info(String.format("The tracker has been updated with %d new %s.",dataset.size(), English.plural("value", dataset.size())));
    }
    /**
     * The executable that finalizes the most recent data entry in each iteration. 
     */
    @Override
    public void run(){
        update(measure());
    }
    /**
     * Stops tracking the activity.
     */
    @Override
    public void shutdown(){
        executor.shutdownNow();
        log.info("Activity Tracker shut down.");
    }
    /**
     * A wrapper class that contains a data point for each guild.
     */
    protected static class Dataset extends Object2ObjectOpenHashMap<Guild,Data>{
        private static final long serialVersionUID = 1L;
    }
    /**
     * A wrapper class that transforms an entry into a double.
     */
    public interface Entry extends Function<Data,Double>{}
    /**
     * The class that stores all relevant information during the runtime.
     */
    protected static class Data{
        /**
         * The minute when the entry was created.
         */
        protected Minute time;
        /**
         * The total amount of member in the guild.
         */
        protected long all_member;
        /**
         * The amount of member that have been online.
         */
        protected long member_online;
        /**
         * The amount of submissions in each channel.
         */
        protected Map<TextChannel,Long> posts;
        /**
         * @param time the time when the data point was created.
         * @param all_member the total amount of member in the guild.
         * @param online the amount of member that are online.
         */
        public Data(Minute time,long all_member, long online){
            this.time = time;
            this.all_member = all_member;
            this.member_online = online;
            this.posts = new Object2LongOpenHashMap<>();
        }
    }
    /**
     * An implementation of a ring buffer backed by a linked list. The newest
     * element will always be at the end of the list.
     * @param <T> the type of entries in the buffer.
     */
    protected static class EvictingLinkedQueue<T> extends AbstractQueue<T>{
        /**
         * The internal list.
         */
        protected List<T> list = new LinkedList<>();
        /**
         * The size of the ring buffer.
         */
        protected final long size;
        /**
         * @param interval the amount of elements per day.
         */
        public EvictingLinkedQueue(int interval){
            this.size = MINUTES.convert(1, DAYS) / interval;
        }
        /**
         * @return an iterator of the ringbuffer, starting from the start of the internal list.
         */
        @Override
        public Iterator<T> iterator() {
            return list.iterator();
        }
        /**
         * @return the amount of elements in the buffer. 
         */
        @Override
        public int size() {
            return list.size();
        }
        /**
         * Inserts the new element into the buffer and removes the oldest one, if it is as its maximum.
         * @param e the new element.
         * @return true
         */
        @Override
        public boolean offer(T e) {
            if(list.size() == size){
                this.poll();
            }
            list.add(list.size(),e);
            return true;
        }
        /**
         * Removes the first element in the internal list.
         * @return the first element in the internal list.
         */
        @Override
        public T poll() {
            return list.isEmpty() ? null : list.remove(0);
        }
        /**
         * @return the first element in the internal list.
         */
        @Override
        public T peek() {
            return list.isEmpty() ? null : list.get(0);
        }
        /**
         * @return the last element in the internal list.
         */
        public T tail(){
            return list.isEmpty() ? null : list.get(list.size()-1);
        }
    }
}