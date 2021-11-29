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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.eclipse.jdt.annotation.Nullable;
import vartas.chart.line.$factory.LineChartFactory;
import vartas.chart.line.LineChart;
import vartas.chart.line.Position;
import zav.discord.blanc.databind.TextChannelValueObject;
import zav.discord.blanc.databind.activity.DataPointValueObject;
import zav.discord.blanc.view.GuildView;

/**
 * The builder for constructing the line chart over the corresponding guild.<br>
 * By using the cached snapshots, we can recreate and plot the recent history of the guild.<br>
 * The chart itself consists two range axis. The left axis plots the accumulated number of
 * messages per minute over all text channels. If individual channels have been specified, the
 * chart also includes their activity separately. The right axis plots the total amount of
 * members as well as the ones that are currently online.
 */
public class ActivityChart {
  private final Cache<LocalDateTime, DataPointValueObject> entries = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofDays(1))
        .build();
    
  public void add(DataPointValueObject data) {
    this.entries.put(LocalDateTime.now(), data);
  }
  
  /**
   * Implements the builder pattern for the activity chart.<br>
   * A chart will show the activity in all text channels of a single guild. If desired, additional
   * text channels can be provided as an argument, which will be displayed as additional lines
   * in the resulting chart.
   */
  public class Builder {
    /**
     * A collection of text channels whose activity is plotted separately.
     */
    private List<TextChannelValueObject> channels = Collections.emptyList();
    /**
     * The final chart containing the accumulated data.
     */
    private @Nullable LineChart chart;
  
    /**
     * Creates a new chart instance over the provided guild.
     *
     * @param guild A view over a single guild.
     * @return A reference to this builder instance.
     */
    public Builder withGuild(GuildView guild) {
      this.chart = LineChartFactory.create(
            JFreeLineChart::new,
            ChronoUnit.MINUTES,
            new ArrayList<>(),
            "Time (UTC)",
            "#Messages/min",
            Optional.of("Members"),
            guild.getAbout().getName()
      );
      return this;
    }

    public Builder withChannels(List<TextChannelValueObject> channels) {
      this.channels = channels;
      return this;
    }

    /**
     * Constructs the plot and transforms it into a {@link BufferedImage}.<br>
     * The plot contains three base entries:
     * <ul>
     *     <li>The total amount of members.</li>
     *     <li>The amount of members that are online.</li>
     *     <li>The total amount of messages per minute in the entire guild.</li>
     * </ul>
     * Additionally, the activity of each explicitly stated {@link TextChannelValueObject} is
     * included as well.
     *
     * @param bounds The dimension of the {@link BufferedImage}.
     * @return A {@link BufferedImage} containing a plot of the activity.
     */
    public BufferedImage build(Rectangle bounds) {
      assert chart != null;
      
      //Fill the chart with the corresponding data sets
      entries.asMap().forEach((key, value) -> {
        chart.addEntries(value.getMembersCount(), key, "#Members", Position.RIGHT);
        chart.addEntries(value.getMembersOnline(), key, "#Members Online", Position.RIGHT);
        chart.addEntries(value.getActivity(), key, "#Messages/min", Position.LEFT);
        value.getChannelActivity().forEach((channel, activity) -> {
          if (channels.contains(channel)) {
            chart.addEntries(activity, key, channel.getName(), Position.LEFT);
          }
        });
      });
  
      return chart.create(bounds.width, bounds.height);
    }
  }
}
