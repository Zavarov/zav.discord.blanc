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

import com.google.common.collect.ListMultimap;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.pagination.Paginator;
import net.dv8tion.jda.core.JDA.Status;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.offlinejraw.OfflineNetworkAdapter;
import vartas.offlinejraw.OfflineSubmissionListingResponse;
import vartas.offlinejraw.OfflineSubmissionListingResponse.OfflineSubmission;
import vartas.offlinejraw.OfflineSubredditResponse;
import vartas.reddit.PushshiftWrapper.CompactComment;
import vartas.reddit.PushshiftWrapper.CompactSubmission;

/**
 *
 * @author u/Zavarov
 */
public class AbstractEnvironmentTest {
    static AbstractEnvironment environment;
    @BeforeClass
    public static void setUp(){
        environment = new OfflineEnvironment();
    }
    @Test
    public void configTest(){
        assertEquals(environment.config,environment.config());
    }
    @Test
    public void credentialsTest(){
        assertEquals(environment.credentials,environment.credentials());
    }
    @Test
    public void permissionTest(){
        assertEquals(environment.permission,environment.permission());
    }
    @Test
    public void statusTest(){
        assertEquals(environment.status,environment.status());
    }
    @Test
    public void commandTest(){
        assertEquals(environment.command,environment.command());
    }
    @Test
    public void grammarTest(){
        assertEquals(environment.grammar,environment.grammar());
    }
    @Test
    public void onlinestatusTest(){
        assertEquals(environment.onlinestatus(),OnlineStatus.ONLINE);
    }
    @Test
    public void onlinestatusEmptyTest(){
        List<Communicator> comm = new ArrayList<>(environment.shards);
        environment.shards.clear();
        
        assertEquals(environment.onlinestatus(),OnlineStatus.UNKNOWN);
        
        environment.shards.addAll(comm);
    }
    @Test
    public void onlinestatusChangeTest(){
        assertEquals(environment.onlinestatus(),OnlineStatus.ONLINE);
        environment.onlinestatus(OnlineStatus.IDLE);
        assertEquals(environment.onlinestatus(),OnlineStatus.IDLE);
    }
    @Test
    public void gameTest(){
        assertNull(environment.game());
    }
    @Test
    public void gameChangeTest(){
        assertNull(environment.game());
        environment.game(Game.playing("game"));
        assertEquals(environment.game(),Game.playing("game"));
    }
    @Test
    public void commTest(){
        assertEquals(environment.shards.get(0), environment.comm(1 << 23));
        assertEquals(environment.shards.get(1), environment.comm(1 << 22));
    }
    @Test
    public void guildTest(){
        JDAImpl j1 = (JDAImpl)environment.shards.get(0).jda();
        GuildImpl g1 = new GuildImpl(j1,1L);
        j1.getGuildMap().put(g1.getIdLong(), g1);
        JDAImpl j2 = (JDAImpl)environment.shards.get(1).jda();
        GuildImpl g2 = new GuildImpl(j2,2L);
        j2.getGuildMap().put(g2.getIdLong(), g2);
        
        assertEquals(environment.guild().size(),2);
        assertTrue(environment.guild().containsAll(Arrays.asList(g1,g2)));
    }
    @Test
    public void compactCommentDateTest(){
        ListMultimap<String, CompactComment> comments;
        comments = environment.compactComment(Instant.ofEpochMilli(1483228800000L));
        
        assertEquals(comments.keySet().size(),1);
        assertTrue(comments.containsKey("subreddit"));
        assertEquals(comments.get("subreddit").size(),1);
        assertEquals(comments.get("subreddit").get(0).getId(),"id2");
    }
    @Test
    public void compactCommentSubredditTest(){
        ListMultimap<Instant, CompactComment> comments;
        comments = environment.compactComment("subreddit");
        
        assertEquals(comments.keySet().size(),3);
        assertTrue(comments.containsKey(Instant.ofEpochMilli(0L)));
        assertTrue(comments.containsKey(Instant.ofEpochMilli(1483228800000L)));
        assertTrue(comments.containsKey(Instant.ofEpochMilli(1546300800000L)));
        assertEquals(comments.get(Instant.ofEpochMilli(0L)).size(),1);
        assertEquals(comments.get(Instant.ofEpochMilli(1483228800000L)).size(),1);
        assertEquals(comments.get(Instant.ofEpochMilli(1546300800000L)).size(),1);
        assertEquals(comments.get(Instant.ofEpochMilli(0L)).get(0).getId(),"id1");
        assertEquals(comments.get(Instant.ofEpochMilli(1483228800000L)).get(0).getId(),"id2");
        assertEquals(comments.get(Instant.ofEpochMilli(1546300800000L)).get(0).getId(),"id3");
    }
    @Test
    public void compactSubmissionDateTest(){
        ListMultimap<String, CompactSubmission> submission;
        submission = environment.compactSubmission(Instant.ofEpochMilli(1483228800000L));
        
        assertEquals(submission.keySet().size(),1);
        assertTrue(submission.containsKey("subreddit"));
        assertEquals(submission.get("subreddit").size(),1);
        assertEquals(submission.get("subreddit").get(0).getId(),"id2");
    }
    @Test
    public void compactSubmissionSubredditTest(){
        ListMultimap<Instant, CompactSubmission> submission;
        submission = environment.compactSubmission("subreddit");
        
        assertEquals(submission.keySet().size(),3);
        assertTrue(submission.containsKey(Instant.ofEpochMilli(0L)));
        assertTrue(submission.containsKey(Instant.ofEpochMilli(1483228800000L)));
        assertTrue(submission.containsKey(Instant.ofEpochMilli(1546300800000L)));
        assertEquals(submission.get(Instant.ofEpochMilli(0L)).size(),1);
        assertEquals(submission.get(Instant.ofEpochMilli(1483228800000L)).size(),1);
        assertEquals(submission.get(Instant.ofEpochMilli(1546300800000L)).size(),1);
        assertEquals(submission.get(Instant.ofEpochMilli(0L)).get(0).getId(),"id1");
        assertEquals(submission.get(Instant.ofEpochMilli(1483228800000L)).get(0).getId(),"id2");
        assertEquals(submission.get(Instant.ofEpochMilli(1546300800000L)).get(0).getId(),"id3");
    }
    @Test
    public void shutdownTest(){
        environment.shutdown();
        assertEquals(environment.shards.get(0).jda().getStatus(),Status.SHUTDOWN);
        assertEquals(environment.shards.get(1).jda().getStatus(),Status.SHUTDOWN);
        
        //Restart everything
        setUp();
    }
    @Test
    public void submissionTest(){
        OfflineSubmission sub = new OfflineSubmission()
                .addNumComments(10)
                .addThumbnailHeight(0)
                .addThumbnailWidth(0)
                .addThumbnail(null)
                .addSelftext("selftext")
                .addLinkFlairText(null)
                .addOver18(true)
                .addSpoilerTest(true)
                .addLikes()
                .addUrl("sub.jpg")
                .addTitle("title")
                .addSubredditId("subreddit_id")
                .addSubreddit("subreddit")
                .addPermalink("permalink")
                .addId("id")
                .addName("submission")
                .addAuthor("author")
                .addDomain("i.redd.it")
                .addDistinguished()
                .addCreatedUtc(1000L);
        
        ((OfflineNetworkAdapter)environment.adapter())
                .addResponse(new OfflineSubmissionListingResponse()
                .addSubmission(sub)
                .addSubreddit("subreddit")
                .addAfter("submission")
                .addAfterRequest(null)
                .addLimit(Paginator.RECOMMENDED_MAX_LIMIT)
                .addSort("new")
                .build());
        
        
        ((OfflineNetworkAdapter)environment.adapter())
                .addResponse(new OfflineSubmissionListingResponse()
                .addSubreddit("subreddit")
                .addAfterRequest("submission")
                .addAfter(null)
                .addLimit(Paginator.RECOMMENDED_MAX_LIMIT)
                .addSort("new")
                .build());
        
        
        List<Submission> submissions = environment.submission("subreddit", Instant.ofEpochSecond(0), Instant.ofEpochSecond(2000));
        assertEquals(submissions.size(),1);
        assertEquals(submissions.get(0).getId(),"id");
    }
    @Test
    public void subredditTest(){
        ((OfflineNetworkAdapter)environment.adapter())
                .addResponse(new OfflineSubredditResponse()
                .addUrl("https://www.reddit.com/r/stuff")
                .addName("stuff")
                .addCreatedUtc(System.currentTimeMillis()/1000)
                .addId("id")
                .addAccountsActive(10)
                .addBannerImg("https://www.reddit.com/banner.jpg")
                .addDisplayName("stuff")
                .addPublicDescription("description")
                .addSubscribers(20)
                .addTitle("title").build());
        
        Subreddit subreddit = environment.subreddit("stuff");
        assertEquals(subreddit.getUrl(),"https://www.reddit.com/r/stuff");
    }
    @Test
    public void adapterTest(){
        assertEquals(environment.adapter, environment.adapter());
    }
}
