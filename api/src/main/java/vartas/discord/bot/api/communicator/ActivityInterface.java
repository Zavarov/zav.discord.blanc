package vartas.discord.bot.api.communicator;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.jfree.chart.JFreeChart;

import java.util.Collection;

/*
 * Copyright (C) 2019 Zavarov
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
public interface ActivityInterface {
    /**
     * Updates the activity tracker by a new message in the given channel.
     * @param channel the channel in which activity was observed.
     */
    void activity(TextChannel channel);
    /**
     * @param guild the guild the chart is plotted over.
     * @param channels the channels that are also plotted.
     * @return a chart over the activity in the guild and also the selected channels.
     */
    JFreeChart activity(Guild guild, Collection<TextChannel> channels);
}
