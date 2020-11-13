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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.AbstractTest;
import vartas.discord.blanc.mock.ActivityMock;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ActivityTest extends AbstractTest {
    Activity activity;
    List<GuildActivity> guildActivities;
    @BeforeEach
    public void setUp(){
        activity = new ActivityMock();
        guildActivities = new ArrayList<>();

        for(int i = 1 ; i < 5 ; ++i) {
            GuildActivity guildActivity = new GuildActivity();
            guildActivity.putChannelActivity(textChannel, (double)i * 4 + 4);
            guildActivity.setMembersOnline(i * 2);
            guildActivity.setMembersCount(i * 2 + 2);
            guildActivity.setActivity(i * 4 + 6);
            guildActivities.add(guildActivity);
        }
    }

    @Test
    public void testGetRealThis(){
        assertThat(activity.getRealThis()).isEqualTo(activity);
    }

    @Test
    public void testCountMessage(){
        assertThat(activity.messages.isEmpty());

        activity.countMessage(textChannel);

        assertThat(activity.messages).containsExactly(new AbstractMap.SimpleImmutableEntry<>(textChannel, 1L));
    }

    @Test
    public void testCreate() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        for(GuildActivity guildActivity : guildActivities) {
            activity.putActivity(now, guildActivity);
            now = now.minusDays(1);
        }

        BufferedImage image = activity.create(guild, Collections.singletonList(textChannel), new Rectangle(1024,768));
        ImageIO.write(image, "png", new File("target/Activity.png"));
    }
}
