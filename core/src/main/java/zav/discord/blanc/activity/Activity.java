/*
 * Copyright (c) 2020 Zavarov
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

package zav.discord.blanc.activity;

import chart.line.JFreeLineChart;
import vartas.chart.line.$factory.LineChartFactory;
import vartas.chart.line.LineChart;
import vartas.chart.line.Position;
import zav.discord.blanc.Guild;
import zav.discord.blanc.TextChannel;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The activity within a ${@link Guild} describes both the amount of members and their participation.
 * <p>
 * Throughout the lifetime of the ${@link Guild}, the total amount of members and its subset that are online are counted
 * periodically. Additionally, it also keeps track of the messages sent in the individual text channels. The activity in
 * those is then computed by taking all messages that have been received within a specific interval and then dividing it
 * by the duration to get the number of messages per minute.
 */
@Nonnull
public abstract class Activity extends ActivityTOP{
    /**
     * This map keeps track of all messages that have been received in the individual text channels. One may be tempted
     * to use the internal cache via {@link TextChannel#retrieveMessages()} ()}, but the we are left to the mercy of its
     * behaviour and messages may be discarded before the activity has been calculated. In order to minimize the
     * overhead, we only keep track of the message occurrences and not their content.
     * <p>
     * The downside is, however, that we manually have to clear the map in order to prevent it from growing
     * indefinitely.
     */
    @Nonnull
    protected final Map<TextChannel, Long> messages = new ConcurrentHashMap<>();

    /**
     * Increases the count for the number of messages in the associated {@link TextChannel} by one.
     * @see #messages
     * @param channel The {@link TextChannel} in which the new message was received.
     */
    public void countMessage(@Nonnull TextChannel channel){
        messages.merge(channel, 1L, Long::sum);
    }

    /**
     * Part of the visitor pattern to grant access to the explicit implementation of the individual types.
     * @return The current instance.
     */
    @Override
    @Nonnull
    public Activity getRealThis() {
        return this;
    }

    /**
     * Plots the activity of the corresponding {@link Guild} using the cached snapshots.
     * @see #getActivity()
     * @param guild The {@link Guild} associated with this {@link Activity}.
     * @param textChannels A subset of text channels belonging to the specified {@link Guild}. Additionally to the
     *                     normal entry, the chart will also include the activity of the individual channels.
     * @param bounds The dimensions of the {@link BufferedImage}.
     * @return A line chart plotting the recent {@link Guild} activity..
     */
    @Nonnull
    public BufferedImage create(@Nonnull Guild guild, @Nonnull List<TextChannel> textChannels, @Nonnull Rectangle bounds){
        return new ChartBuilder(guild, textChannels).build(bounds);
    }

    /**
     * The builder for constructing the line chart over the corresponding {@link Guild}.
     * <p>
     * By using the cached snapshots, we can recreate and plot the recent history of the {@link Guild}. The chart
     * itself consists two range axis. The left axis plots the accumulated number of messages per minute over all
     * text channels. If individual channels have been specified, the chart also includes their activity separately.
     * The right axis plots the total amount of members as well as the ones that are currently online.
     */
    @Nonnull
    private class ChartBuilder{
        /**
         * A collection of text channels whose activity is plotted separately.
         */
        @Nonnull
        private final List<TextChannel> channels;
        /**
         * The final chart containing the accumulated data.
         */
        @Nonnull
        private final LineChart chart;

        /**
         * Creates a fresh chart.
         * @param guild The {@link Guild} associated with the chart and the cached {@link Guild} snapshots.
         * @param channels A collection of text channels included in the chart.
         */
        public ChartBuilder(@Nonnull Guild guild, @Nonnull List<TextChannel> channels){
            this.channels = channels;
            this.chart = LineChartFactory.create(
                    JFreeLineChart::new,
                    ChronoUnit.MINUTES,
                    new ArrayList<>(),
                    "Time (UTC)",
                    "#Messages/min",
                    Optional.of("Members"),
                    guild.getName()
            );
        }

        /**
         * Constructs the plot and transforms it into a {@link BufferedImage}.<br>
         * The plot contains three base entries:
         * <ul>
         *     <li>The total amount of members.</li>
         *     <li>The amount of members that are online.</li>
         *     <li>The total amount of messages per minute in the entire guild.</li>
         * </ul>
         * Additionally, the activity of each explicitly stated {@link TextChannel} is included as well.
         * @param bounds The dimension of the {@link BufferedImage}.
         * @return A {@link BufferedImage} containing a plot of the activity.
         */
        @Nonnull
        public BufferedImage build(@Nonnull Rectangle bounds){
            //Fill the chart with the corresponding data sets
            asMapActivity().forEach((key, value) -> {
                chart.addEntries(value.getMembersCount(), key, "#Members", Position.RIGHT);
                chart.addEntries(value.getMembersOnline(), key, "#Members Online", Position.RIGHT);
                chart.addEntries(value.getActivity(), key, "#Messages/min", Position.LEFT);
                value.forEachChannelActivity((channel, activity) -> {
                    if(channels.contains(channel))
                        chart.addEntries(activity, key, channel.getName(), Position.LEFT);
                });
            });

            return chart.create(bounds.width, bounds.height);
        }
    }
}
