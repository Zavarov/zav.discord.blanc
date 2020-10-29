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

package vartas.discord.blanc.activity;

import chart.line.JFreeLineChart;
import org.jfree.chart.JFreeChart;
import vartas.chart.line.LineChart;
import vartas.chart.line.Position;
import vartas.chart.line.factory.LineChartFactory;
import vartas.chart.line.factory.NumberDatasetFactory;
import vartas.discord.blanc.Guild;
import vartas.discord.blanc.TextChannel;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;

/**
 * This class records the activity in a single {@link Guild} over the course of several hours and is able to visualize
 * them.
 */
public class Activity extends ActivityTOP{
    /**
     * In order to avoid the ambiguity causes by the message cache of the text channel, all received messages are
     * stored in a separate map. To minimize the overhead, only the count is stored. <b>Note:</b> The map has to be
     * cleared manually to prevent it from growing indefinitely.
     */
    protected final Map<TextChannel, Long> messages = new HashMap<>();

    /**
     * Increases the number of received messages in the specified {@link TextChannel} by one.
     * @param channel A {@link TextChannel} in which a new message was received.
     */
    public void countMessage(TextChannel channel){
        messages.merge(channel, 1L, Long::sum);
    }

    /**
     * Creates a new {@link JFreeChart} containing the activity of the specified {@link Guild}.
     * @param guild The {@link Guild} associated with this {@link Activity}.
     * @param textChannels A subset of {@link TextChannel TextChannels} of the specified {@link Guild}.
     *                     The generated chart will also include the activity within those individual channels..
     * @param bounds The dimensions of the {@link BufferedImage}.
     * @return A {@link BufferedImage} plotting the {@link Guild} activity over the past hours.
     */
    @Nonnull
    public BufferedImage create(@Nonnull Guild guild, @Nonnull List<TextChannel> textChannels, @Nonnull Rectangle bounds){
        return new ChartBuilder(guild, textChannels).build(bounds);
    }

    /**
     * Part of the visitor pattern to grant access to the explicit implementation of the individual types.
     * @return The current instance.
     */
    @Override
    public Activity getRealThis() {
        return this;
    }

    /**
     * This class is used to construct a chart based on the cached {@link Guild} snapshots.
     */
    private class ChartBuilder{
        /**
         * A collection of text channels whose activity is also included in the chart.
         */
        @Nonnull
        private final List<TextChannel> channels;
        /**
         * The chart containing the accumulated data.
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
                    "Time",
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
        public BufferedImage build(@Nonnull Rectangle bounds){
            //Fill the chart with the corresponding data sets
            asMapActivity().forEach((key, value) -> {
                chart.addEntries(NumberDatasetFactory.create(value.getMembersCount(), key, "#Members", Position.RIGHT));
                chart.addEntries(NumberDatasetFactory.create(value.getMembersOnline(), key, "#Members Online", Position.RIGHT));
                chart.addEntries(NumberDatasetFactory.create(value.getActivity(), key, "#Messages/min", Position.LEFT));
                value.forEachChannelActivity((channel, activity) -> {
                    if(channels.contains(channel))
                        chart.addEntries(NumberDatasetFactory.create(activity, key, channel.getName(), Position.LEFT));
                });
            });


            return chart.create(bounds.width, bounds.height);
        }
    }
}
