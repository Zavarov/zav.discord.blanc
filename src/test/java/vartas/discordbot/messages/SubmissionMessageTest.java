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
package vartas.discordbot.messages;

import java.awt.Color;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.OfflineInstance;
import vartas.offlinejraw.OfflineNetworkAdapter;
import vartas.offlinejraw.OfflineRateLimiter;
import vartas.offlinejraw.OfflineSubmissionListingResponse;
import vartas.offlinejraw.OfflineSubmissionListingResponse.OfflineSubmission;
import vartas.reddit.RedditBot;
import vartas.xml.XMLCredentials;

/**
 *
 * @author u/Zavarov
 */
public class SubmissionMessageTest {
    List<Submission> submissions;
    OfflineInstance instance;
    Date start;
    Date end;
    @Before
    public void setUp(){
        instance = new OfflineInstance();
        
        XMLCredentials credentials = XMLCredentials.create(new File("src/test/resources/credentials.xml"));
        OfflineNetworkAdapter adapter = new OfflineNetworkAdapter();
        RedditBot bot = new RedditBot(credentials, adapter);
        bot.getClient().setRateLimiter(new OfflineRateLimiter());
        
        start = new Date(System.currentTimeMillis() + 24*60*60*1000);
        end = new Date(System.currentTimeMillis()- 24*60*60*1000);
        
        
        OfflineSubmission sub1 = new OfflineSubmission()
                .addNumComments(10)
                .addThumbnailHeight(0)
                .addThumbnailWidth(0)
                .addThumbnail("self")
                .addSelftext("selftext1")
                .addOver18(false)
                .addSpoilerTest(false)
                .addLikes()
                .addUrl("https://www.reddit.com/sub1.jpg")
                .addTitle("[nsfw][spoiler]title1")
                .addSubredditId("subreddit_id")
                .addSubreddit("other")
                .addPermalink("permalink1")
                .addId("id1")
                .addName("submission1")
                .addAuthor("author1")
                .addDomain("i.redd.it")
                .addDistinguished()
                .addCreatedUtc(System.currentTimeMillis()/1000-100);
        
        OfflineSubmission sub2 = new OfflineSubmission()
                .addNumComments(10)
                .addThumbnailHeight(10)
                .addThumbnailWidth(10)
                .addThumbnail("https://www.reddit.com/thumbnail2.jpg")
                .addSelftext("selftext2")
                .addOver18(false)
                .addSpoilerTest(false)
                .addLikes()
                .addUrl("https://www.reddit.com/sub2.jpg")
                .addTitle("title2")
                .addSubredditId("subreddit_id")
                .addSubreddit("other")
                .addPermalink("permalink2")
                .addLinkFlairText("nsfw")
                .addId("id2")
                .addName("submission2")
                .addAuthor("author2")
                .addDomain("i.redd.it")
                .addDistinguished()
                .addCreatedUtc(System.currentTimeMillis()/1000-200);
        
        OfflineSubmission sub3 = new OfflineSubmission()
                .addNumComments(10)
                .addThumbnailHeight(10)
                .addThumbnailWidth(10)
                .addThumbnail("https://www.reddit.com/thumbnail3.jpg")
                .addSelftext("selftext3")
                .addOver18(false)
                .addSpoilerTest(false)
                .addLikes()
                .addUrl("https://www.reddit.com/sub3.jpg")
                .addTitle("title3")
                .addSubredditId("subreddit_id")
                .addSubreddit("other")
                .addPermalink("permalink3")
                .addLinkFlairText("spoiler")
                .addId("id3")
                .addName("submission3")
                .addAuthor("author3")
                .addDomain("i.redd.it")
                .addDistinguished()
                .addCreatedUtc(System.currentTimeMillis()/1000-300);
        
        OfflineSubmission sub4 = new OfflineSubmission()
                .addNumComments(10)
                .addThumbnailHeight(10)
                .addThumbnailWidth(10)
                .addThumbnail("https://www.reddit.com/thumbnail4.jpg")
                .addSelftext("selftext4")
                .addOver18(false)
                .addSpoilerTest(false)
                .addLikes()
                .addUrl("https://www.reddit.com/sub4.jpg")
                .addTitle("title4")
                .addSubredditId("subreddit_id")
                .addSubreddit("other")
                .addPermalink("permalink4")
                .addId("id4")
                .addName("submission4")
                .addAuthor("author4")
                .addDomain("i.redd.it")
                .addDistinguished()
                .addCreatedUtc(System.currentTimeMillis()/1000-300);
        
        OfflineSubmission sub5 = new OfflineSubmission()
                .addNumComments(10)
                .addThumbnailHeight(0)
                .addThumbnailWidth(0)
                .addThumbnail(null)
                .addSelftext("selftext5")
                .addOver18(true)
                .addSpoilerTest(false)
                .addLikes()
                .addUrl("https://www.reddit.com/sub5.jpg")
                .addTitle("title5")
                .addSubredditId("subreddit_id")
                .addSubreddit("other")
                .addPermalink("permalink5")
                .addId("id5")
                .addName("submission5")
                .addAuthor("author5")
                .addDomain("i.redd.it")
                .addDistinguished()
                .addCreatedUtc(System.currentTimeMillis()/1000-300);
        
        OfflineSubmission sub6 = new OfflineSubmission()
                .addNumComments(10)
                .addThumbnailHeight(0)
                .addThumbnailWidth(0)
                .addThumbnail(null)
                .addSelftext("selftext6")
                .addOver18(false)
                .addSpoilerTest(true)
                .addLikes()
                .addUrl("https://www.reddit.com/sub6.jpg")
                .addTitle("title6")
                .addSubredditId("subreddit_id")
                .addSubreddit("other")
                .addPermalink("permalink6")
                .addId("id6")
                .addName("submission6")
                .addAuthor("author6")
                .addDomain("i.redd.it")
                .addDistinguished()
                .addCreatedUtc(System.currentTimeMillis()/1000-300);
        
        OfflineSubmission sub7 = new OfflineSubmission()
                .addNumComments(10)
                .addThumbnailHeight(0)
                .addThumbnailWidth(0)
                .addThumbnail(null)
                .addSelftext("selftext7")
                .addOver18(false)
                .addSpoilerTest(false)
                .addLikes()
                .addUrl("https://www.reddit.com/sub7.jpg")
                .addTitle(StringUtils.repeat("a", MessageEmbed.TITLE_MAX_LENGTH+1))
                .addSubredditId("subreddit_id")
                .addSubreddit("other")
                .addPermalink("permalink7")
                .addId("id7")
                .addName("submission7")
                .addAuthor("author7")
                .addDomain("i.redd.it")
                .addDistinguished()
                .addCreatedUtc(System.currentTimeMillis()/1000-300);
        
        OfflineSubmission sub8 = new OfflineSubmission()
                .addNumComments(10)
                .addThumbnailHeight(10)
                .addThumbnailWidth(10)
                .addThumbnail("image")
                .addSelftext(StringUtils.repeat("a", MessageEmbed.TEXT_MAX_LENGTH+1))
                .addOver18(false)
                .addSpoilerTest(false)
                .addLikes()
                .addUrl("https://www.reddit.com/sub8.jpg")
                .addTitle("ttitle")
                .addSubredditId("subreddit_id")
                .addSubreddit("other")
                .addPermalink("permalink8")
                .addId("id8")
                .addName("submission8")
                .addAuthor("author8")
                .addDomain("i.redd.it")
                .addDistinguished()
                .addCreatedUtc(System.currentTimeMillis()/1000-300);
        
        adapter.addResponse(new OfflineSubmissionListingResponse()
                .addSubmission(sub1)
                .addSubmission(sub2)
                .addSubmission(sub3)
                .addSubmission(sub4)
                .addSubmission(sub5)
                .addSubmission(sub6)
                .addSubmission(sub7)
                .addSubmission(sub8)
                .addSubreddit("other")
                .addAfterRequest(null)
                .addAfter("submission8")
                .addLimit(Paginator.RECOMMENDED_MAX_LIMIT)
                .addSort("new")
                .build());
        adapter.addResponse(new OfflineSubmissionListingResponse()
                .addSubreddit("other")
                .addAfterRequest("submission8")
                .addAfter("null")
                .addLimit(Paginator.RECOMMENDED_MAX_LIMIT)
                .addSort("new")
                .build());
        
        DefaultPaginator<Submission> paginator = bot.getClient().subreddit("other").posts()
                .limit(Paginator.RECOMMENDED_MAX_LIMIT)
                .sorting(SubredditSort.NEW)
                .timePeriod(TimePeriod.ALL)
                .build();
        
        submissions = paginator.accumulateMerged(1);
    }
    @Test
    public void createNsfwMessageTest(){
        Message message = SubmissionMessage.create(submissions.get(4)).build();
        MessageEmbed embed = message.getEmbeds().get(0);
        assertTrue(embed.getTitle().contains("[NSFW]"));
    }
    @Test
    public void createNsfwTitleMessageTest(){
        Message message = SubmissionMessage.create(submissions.get(0)).build();
        MessageEmbed embed = message.getEmbeds().get(0);
        assertTrue(embed.getTitle().toLowerCase(Locale.ENGLISH).contains("[nsfw]"));
    }
    @Test
    public void createNsfwFlairMessageTest(){
        Message message = SubmissionMessage.create(submissions.get(1)).build();
        MessageEmbed embed = message.getEmbeds().get(0);
        assertTrue(embed.getTitle().toLowerCase(Locale.ENGLISH).contains("[nsfw]"));
    }
    @Test
    public void createSpoilerMessageTest(){
        Message message = SubmissionMessage.create(submissions.get(5)).build();
        MessageEmbed embed = message.getEmbeds().get(0);
        assertTrue(embed.getTitle().contains("[Spoiler]"));
        
    }
    @Test
    public void createSpoilerTitleMessageTest(){
        Message message = SubmissionMessage.create(submissions.get(0)).build();
        MessageEmbed embed = message.getEmbeds().get(0);
        assertTrue(embed.getTitle().toLowerCase(Locale.ENGLISH).contains("[spoiler]"));
    }
    @Test
    public void createSpoilerFlairMessageTest(){
        Message message = SubmissionMessage.create(submissions.get(2)).build();
        MessageEmbed embed = message.getEmbeds().get(0);
        assertTrue(embed.getTitle().toLowerCase(Locale.ENGLISH).contains("[spoiler]"));
    }
    @Test
    public void createNormalMessageTest(){
        Message message = SubmissionMessage.create(submissions.get(3)).build();
        MessageEmbed embed = message.getEmbeds().get(0);
        assertNotEquals(embed.getColor(),Color.RED);
        assertNotEquals(embed.getColor(),new Color(1,0,0));
        assertEquals(embed.getThumbnail().getUrl(),"https://www.reddit.com/thumbnail4.jpg");
    }
    @Test
    public void createMessageTitleTooLong(){
        Message message = SubmissionMessage.create(submissions.get(6)).build();
        MessageEmbed embed = message.getEmbeds().get(0);
        assertTrue(embed.getTitle().endsWith("..."));
    }
    @Test
    public void createMessageSelfTextTooLong(){
        Message message = SubmissionMessage.create(submissions.get(7)).build();
        MessageEmbed embed = message.getEmbeds().get(0);
        assertTrue(embed.getDescription().endsWith("..."));
    }
    @Test
    public void createMessageThumbnailIsNullTest(){
        Message message = SubmissionMessage.create(submissions.get(6)).build();
        MessageEmbed embed = message.getEmbeds().get(0);
        assertNull(embed.getThumbnail());
    }
    @Test
    public void createMessageThumbnailNotUrlTest(){
        Message message = SubmissionMessage.create(submissions.get(7)).build();
        MessageEmbed embed = message.getEmbeds().get(0);
        assertNull(embed.getThumbnail());
        
    }
}