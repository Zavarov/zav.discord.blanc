/*
 * Copyright (c) 2019 Zavarov
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

package vartas.discord.listener;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.atteo.evo.inflector.English;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import vartas.chart.Interval;
import vartas.chart.line.DelegatingLineChart;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * This listener keeps track of the activity in all guilds in the respective shard.
 */
@Nonnull
public class ActivityListener extends ListenerAdapter implements Runnable{
    /**
     * This constant describes the chart entry containing the accumulated activity over all channels in a guild.
     */
    @Nonnull
    private static final String AllChannels = "All Channels";
    /**
     * This constants describes the total number of members in a guild. Bots are excluded.
     */
    @Nonnull
    private static final String AllMembers = "All Members";
    /**
     * This constant describes the number of members that have been online at the time of measurement.
     * The bot can't distinguish between members that are offline or simply invisible.
     * Bots are excluded.
     */
    @Nonnull
    private static final String MembersOnline = "Members Online";
    /**
     * All lines that are treated as singletons, meaning that we don't need to take the average.
     */
    @Nonnull
    private static final Set<String> Singletons = Set.of(AllChannels, AllMembers, MembersOnline);
    /**
     * The log for this class.
     */
    @Nonnull
    private final Logger log = JDALogger.getLog(this.getClass());
    /**
     * The chart for all guilds in the current chart.
     */
    @Nonnull
    private final LoadingCache<Guild, DelegatingLineChart<Long>> charts;
    /**
     * The JDA instance of this shard. Containing all guilds the listener has to keep track of.
     */
    @Nonnull
    private final JDA jda;
    /**
     * Initializes an empty tracker.
     * @param jda the jda of this shard
     * @param stepSize the time between two measurements (in minutes)
     * @throws NullPointerException if {@code jda} is null
     * @throws IllegalArgumentException if {@code stepSize} is smaller than one
     */
    public ActivityListener(@Nonnull JDA jda, int stepSize) throws NullPointerException, IllegalArgumentException{
        Preconditions.checkNotNull(jda);
        Preconditions.checkArgument(stepSize > 0);
        this.jda = jda;
        charts = CacheBuilder.newBuilder().build(CacheLoader.from((guild) -> {
            Preconditions.checkNotNull(guild);

            DelegatingLineChart<Long> chart;

            chart = new DelegatingLineChart<>(
                    //Take the average per minute
                    (label, values) -> Singletons.contains(label) ? values.stream().mapToLong(l -> l).findAny().orElse(0L) : values.stream().mapToLong(l -> l).sum() / stepSize,
                    Duration.ofDays(7)
            );

            chart.setGranularity(ChronoUnit.MINUTES);
            chart.setStepSize(stepSize);
            chart.setInterval(Interval.MINUTE);
            chart.setTitle(String.format("Activity in '%s'", guild.getName()));
            chart.setXAxisLabel("Time");
            chart.setYAxisLabel("Count");

            return chart;
        }));
        log.info("Activity Tracker started.");
    }

    /**
     * Creates an image based on a set of data of the member and channel in this guild.
     * @param guild the guild to get the name.
     * @param channels all channels whose data also has to be plotted.
     * @return the image representing the data.
     * @throws NullPointerException if {@code guild} or {@code channels} is null
     */
    @Nonnull
    public JFreeChart create(@Nonnull Guild guild, @Nonnull Collection<TextChannel> channels){
        Preconditions.checkNotNull(guild);
        Preconditions.checkNotNull(channels);
        List<String> names = new ArrayList<>(channels.size() + 3);

        names.add(AllChannels);
        names.add(AllMembers);
        names.add(MembersOnline);

        channels.stream().map(GuildChannel::getName).forEach(names::add);

        return charts.getUnchecked(guild).create(names);
    }

    /**
     * Updates the total number of guild members and the number of which are online.
     */
    @Override
    public void run(){
        //If we'd use the guilds from the cache we skip servers where nobody has talked
        jda.getGuilds().forEach(this::update);
    }

    /**
     * Counts the total number of members and those that are online in the specified guild.
     * The measured values are stored in the internal cache, keyed by the time at measurement.
     * Bots are excluded.
     * @param guild the guild for which a measurement is made
     * @throws NullPointerException if {@code guild} is null
     */
    private void update(@Nonnull Guild guild) throws NullPointerException{
        Preconditions.checkNotNull(guild);
        DelegatingLineChart<Long> chart = charts.getUnchecked(guild);

        long allMembers = guild.getMembers()
                .stream()
                .filter(m -> !m.getUser().isBot())
                .count();

        long membersOnline = guild.getMembers()
                .stream()
                .filter(m -> !m.getUser().isBot())
                .filter(m -> m.getOnlineStatus() != OnlineStatus.OFFLINE)
                .count();

        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        chart.set(AllMembers, now, Collections.singleton(allMembers));
        chart.set(MembersOnline, now, Collections.singleton(membersOnline));

        log.info(String.format("%d total %s in %s", allMembers, English.plural("member", (int)allMembers), guild.getName()));
        log.info(String.format("%d %s online in %s", membersOnline, English.plural("member", (int)membersOnline), guild.getName()));
    }

    /**
     * Unlike the member count, the number of messages are measured in real time. Every time a message is received,
     * the counter for the respective text channel, at the time of creation, is increased by one.
     * The chart later accumulates all messages within the step size, so we don't have to worry about grouping the
     * messages based on their creation date.
     * As a little side effect, it will happen that
     * that the plot for the messages is ahead of the member plot, due to the latter one not updating in real time.
     * @param event the triggered event.
     */
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        //Ignore bot messages
        if(event.getAuthor().isBot())
            return;

        DelegatingLineChart<Long> chart = charts.getUnchecked(event.getGuild());
        LocalDateTime created = event.getMessage().getTimeCreated().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();

        String channelName = event.getMessage().getTextChannel().getName();

        //Can't use 'update' since there might be no values to update
        long allChannels = chart.get(AllChannels, created).stream().mapToLong(l -> l).findAny().orElse(0L);
        long channel = chart.get(channelName, created).stream().mapToLong(l -> l).findAny().orElse(0L);

        //Update the count by one
        chart.set(AllChannels, created, Collections.singleton(allChannels + 1));
        chart.set(channelName, created, Collections.singleton(channel + 1));
    }

    /**
     * The hook point for the visitor pattern.
     * @param visitor the visitor traversing through this listener
     */
    public void accept(@Nonnull Visitor visitor){
        visitor.handle(this);
    }

    /**
     * The visitor pattern for this listener.
     */
    @Nonnull
    public interface Visitor {
        /**
         * The method that is invoked before the sub-nodes are handled.
         * @param activityListener the corresponding listener
         */
        default void visit(@Nonnull ActivityListener activityListener){}

        /**
         * The method that is invoked to handle all sub-nodes.
         * @param activityListener the corresponding subreddit feed
         */
        default void traverse(@Nonnull ActivityListener activityListener) {}

        /**
         * The method that is invoked after the sub-nodes have been handled.
         * @param activityListener the corresponding subreddit feed
         */
        default void endVisit(@Nonnull ActivityListener activityListener){}

        /**
         * The top method of the listener visitor, calling the remaining visitor methods.
         * The order in which the methods are called is
         * <ul>
         *      <li>visit</li>
         *      <li>traverse</li>
         *      <li>endvisit</li>
         * </ul>
         * @param activityListener the corresponding subreddit feed
         */
        default void handle(@Nonnull ActivityListener activityListener) {
            visit(activityListener);
            traverse(activityListener);
            endVisit(activityListener);
        }
    }
}