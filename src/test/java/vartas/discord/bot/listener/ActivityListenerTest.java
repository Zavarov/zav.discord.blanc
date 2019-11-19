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

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jfree.chart.JFreeChart;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vartas.chart.line.DelegatingLineChart;
import vartas.discord.bot.AbstractBotTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ActivityListenerTest extends AbstractBotTest {
    ActivityListener listener;
    GuildMessageReceivedEvent event;
    DelegatingLineChart<Long> chart;

    @Before
    public void setUp() {
        listener = new ActivityListener(communicator);
        event = new GuildMessageReceivedEvent(jda, 12345L, message);
        chart = listener.charts.getUnchecked(guild);
    }
    @After
    public void cleanUp(){
        user.setBot(false);
    }
    @Test
    public void createTest(){
        Instant first = Instant.now();
        Instant second = first.minus(1, ChronoUnit.MINUTES);

        chart.set(channel.getName(), first, Collections.singletonList(2L));
        chart.set(ActivityListener.AllMembers, first, Collections.singletonList(3L));
        chart.set(ActivityListener.AllChannels, first, Collections.singletonList(4L));
        chart.set(ActivityListener.MembersOnline, first, Collections.singletonList(5L));

        chart.set(channel.getName(), second, Collections.singletonList(2L));
        chart.set(ActivityListener.AllMembers, second, Collections.singletonList(3L));
        chart.set(ActivityListener.AllChannels, second, Collections.singletonList(4L));
        chart.set(ActivityListener.MembersOnline, second, Collections.singletonList(5L));

        save(listener.create(guild, Collections.singletonList(channel)), "Chart");
    }
    @Test
    public void runTest(){
        listener.run();

        assertThat(chart.get(ActivityListener.AllMembers, Instant.now())).contains(1L);
        assertThat(chart.get(ActivityListener.MembersOnline, Instant.now())).contains(0L);
    }
    @Test
    public void onGuildMessageReceivedTest(){
        listener.onGuildMessageReceived(event);

        assertThat(chart.get(ActivityListener.AllChannels, Instant.now())).contains(1L);
        assertThat(chart.get(channel.getName(), Instant.now())).contains(1L);

        user.setBot(true);
        listener.onGuildMessageReceived(event);

        assertThat(chart.get(ActivityListener.AllChannels, Instant.now())).contains(1L);
        assertThat(chart.get(channel.getName(), Instant.now())).contains(1L);
    }

    private void save(JFreeChart chart, String fileName){
        BufferedImage image = chart.createBufferedImage(1024,768);
        Path output = Paths.get("target", fileName+".png");

        try {
            ImageIO.write(image, "png", output.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
