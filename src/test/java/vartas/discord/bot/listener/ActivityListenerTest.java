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

package vartas.discord.bot.listener;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.*;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ActivityListenerTest extends AbstractTest {
    DataMessage message;
    JDAImpl jda;
    UserImpl user;
    MemberImpl member;
    GuildImpl guild;
    TextChannelImpl channel;
    ActivityListener listener;
    GuildMessageReceivedEvent event;

    @Before
    public void setUp() {
        jda = new JDAImpl(authorization){
            @Nonnull
            @Override
            public List<Guild> getGuilds(){
                return Collections.singletonList(guild);
            }
        };
        user = new UserImpl(userId, jda);
        guild = new GuildImpl(jda, guildId){
            @Nonnull
            @Override
            public List<Member> getMembers(){
                return Collections.singletonList(member);
            }
        };
        channel = new TextChannelImpl(channelId, guild);
        member = new MemberImpl(guild, user);

        message = new DataMessage(false, null, null, null){
            @Override
            protected void unsupported() {}
            @Nonnull
            @Override
            public TextChannel getTextChannel(){
                return channel;
            }
            @Nonnull
            @Override
            public UserImpl getAuthor(){
                return user;
            }
        };

        listener = new ActivityListener(jda, 1);
        event = new GuildMessageReceivedEvent(jda, 12345L, message);
    }

    /**
     * Creates a chart with only the self member, which is offline
     */
    @Test
    public void runTest(){
        member.setOnlineStatus(OnlineStatus.OFFLINE);
        listener.run();

        JFreeChart chart = listener.create(guild, Collections.emptyList());

        XYPlot plot = chart.getXYPlot();
        XYDataset dataset = plot.getDataset(0);

        assertThat(dataset.getSeriesCount()).isEqualTo(2);
        //All members
        assertThat(dataset.getY(0, 0).intValue()).isEqualTo(1);
        //Members online
        assertThat(dataset.getY(1, 0).intValue()).isEqualTo(0);
    }


    /**
     * Creates a chart where nothing has been received
     */
    @Test
    public void onGuildBotMessageReceivedTest(){
        user.setBot(true);
        listener.onGuildMessageReceived(event);

        JFreeChart chart = listener.create(guild, Collections.emptyList());

        XYPlot plot = chart.getXYPlot();
        XYDataset dataset = plot.getDataset(0);

        assertThat(dataset.getSeriesCount()).isEqualTo(0);
    }

    /**
     * Creates a chart where only one message has been received
     */
    @Test
    public void onGuildMessageReceivedTest(){
        listener.onGuildMessageReceived(event);

        JFreeChart chart = listener.create(guild, Collections.emptyList());

        XYPlot plot = chart.getXYPlot();
        XYDataset dataset = plot.getDataset(0);

        assertThat(dataset.getSeriesCount()).isEqualTo(1);
        assertThat(dataset.getY(0, 0).intValue()).isEqualTo(1);
    }
}
