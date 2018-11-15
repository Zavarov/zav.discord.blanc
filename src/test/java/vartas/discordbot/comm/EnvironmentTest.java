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
package vartas.discordbot.comm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.TimeZone;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.reddit.PushshiftWrapper.CompactComment;
import vartas.reddit.PushshiftWrapper.CompactSubmission;
import static vartas.reddit.PushshiftWrapper.DATE;

/**
 *
 * @author u/Zavarov
 */
public class EnvironmentTest {
    static AbstractEnvironment environment;
    @BeforeClass
    public static void setUp(){
        environment = new OfflineEnvironment();
    }
    @Test
    public void compactSubmissionTest() throws ParseException{
        List<CompactSubmission> submission;
        submission = environment.compactSubmission(DATE.parse("01-01-2000").toInstant(),"subreddit");
        assertEquals(submission.size(),1);
        assertEquals(submission.get(0).getId(),"id2");
    }
    @Test
    public void compactCommentTest() throws ParseException{
        List<CompactComment> comment;
        comment = environment.compactComment(DATE.parse("01-01-2000").toInstant(),"subreddit");
        assertEquals(comment.size(),1);
        assertEquals(comment.get(0).getId(),"id");
    }
    @Test
    public void commGuildTest(){
        GuildImpl g = new GuildImpl(null, 1<<22);
        assertEquals(environment.comm(g),environment.shards.get(1));
    }
    @Test
    public void commTextChannelTest(){
        GuildImpl g = new GuildImpl(null, 1<<22);
        TextChannelImpl t = new TextChannelImpl(1<<22,g);
        assertEquals(environment.comm(t),environment.shards.get(1));
    }
    @Test
    public void commRoleTest(){
        GuildImpl g = new GuildImpl(null, 1<<22);
        RoleImpl r = new RoleImpl(1<<22,g);
        assertEquals(environment.comm(r),environment.shards.get(1));
    }
    @Test
    public void commStringTest(){
        assertEquals(environment.comm(Long.toString(1<<22)),environment.shards.get(1));
    }
}