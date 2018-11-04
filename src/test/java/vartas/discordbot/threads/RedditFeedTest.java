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
package vartas.discordbot.threads;

import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.dean.jraw.models.Submission;
import net.dean.jraw.pagination.Paginator;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.requests.ErrorResponse;
import net.dv8tion.jda.core.requests.Response;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import org.apache.http.HttpStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.OfflineInstance;
import vartas.offlinejraw.OfflineNetworkAdapter;
import vartas.offlinejraw.OfflineRateLimiter;
import vartas.offlinejraw.OfflineSubmissionListingResponse;
import vartas.offlinejraw.OfflineSubmissionListingResponse.OfflineSubmission;
import vartas.offlinejraw.OfflineSubredditResponse;
import vartas.reddit.RedditBot;
import vartas.xml.XMLCredentials;
import vartas.xml.XMLServer;

/**
 *
 * @author u/Zavarov
 */
public class RedditFeedTest {
    static String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<server>\n" +
"    <entry row=\"reddit\" column=\"subreddit\">\n" +
"        <document>\n" +
"            <entry>1</entry>\n" +
"            <entry>2</entry>\n" +
"        </document>\n" +
"    </entry>\n" +
"    <entry row=\"reddit\" column=\"stuff\">\n" +
"        <document>\n" +
"            <entry>2</entry>\n" +
"            <entry>3</entry>\n" +
"        </document>\n" +
"    </entry>\n" +
"</server>";
    OfflineInstance instance;
    OfflineNetworkAdapter adapter;
    RedditBot bot;
    RedditFeed feed;
    XMLServer server;
    long now;
    
    @BeforeClass
    public static void startUp() throws IOException{
        try (FileWriter writer = new FileWriter(new File("src/test/resources/guilds/0.server"))) {
            writer.write(xml);
        }
    }
    
    @Before
    public void setUp(){
        now = System.currentTimeMillis();
        XMLCredentials credentials = XMLCredentials.create(new File("src/test/resources/credentials.xml"));
        adapter = new OfflineNetworkAdapter();
        bot = new RedditBot(credentials, adapter);
        bot.getClient().setRateLimiter(new OfflineRateLimiter());
        instance = new OfflineInstance();
        server = XMLServer.create(new File("src/test/resources/guilds/0.server"));
        
        feed = new RedditFeed(bot, (g) -> instance.bot);
        adapter.addResponse(new OfflineSubredditResponse()
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
        adapter.addResponse(new OfflineSubmissionListingResponse()
                .addSubreddit("stuff")
                .addAfterRequest(null)
                .addAfter("null")
                .addLimit(Paginator.RECOMMENDED_MAX_LIMIT)
                .addSort("new")
                .build());
        
        OfflineSubmissionListingResponse.OfflineSubmission sub1 = new OfflineSubmissionListingResponse.OfflineSubmission()
                .addNumComments(10)
                .addThumbnailHeight(0)
                .addThumbnailWidth(0)
                .addThumbnail("self")
                .addSelftext("selftext1")
                .addOver18(false)
                .addSpoilerTest(false)
                .addLikes()
                .addUrl("https://www.reddit.com/sub1.jpg")
                .addTitle("title1")
                .addSubredditId("subreddit_id")
                .addSubreddit("subreddit")
                .addPermalink("permalink1")
                .addId("id1")
                .addName("submission1")
                .addAuthor("author1")
                .addDomain("i.redd.it")
                .addDistinguished()
                .addCreatedUtc(1000);
        
        OfflineSubmissionListingResponse.OfflineSubmission sub2 = new OfflineSubmissionListingResponse.OfflineSubmission()
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
                .addSubreddit("subreddit")
                .addPermalink("permalink2")
                .addLinkFlairText("nsfw")
                .addId("id2")
                .addName("submission2")
                .addAuthor("author2")
                .addDomain("i.redd.it")
                .addDistinguished()
                .addCreatedUtc(1000);
        
        OfflineSubmissionListingResponse.OfflineSubmission sub3 = new OfflineSubmissionListingResponse.OfflineSubmission()
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
                .addSubreddit("subreddit")
                .addPermalink("permalink3")
                .addLinkFlairText("spoiler")
                .addId("id3")
                .addName("submission3")
                .addAuthor("author3")
                .addDomain("i.redd.it")
                .addDistinguished()
                .addCreatedUtc(1000);
        
        adapter.addResponse(new OfflineSubmissionListingResponse()
                .addSubmission(sub1)
                .addSubmission(sub2)
                .addSubmission(sub3)
                .addSubreddit("subreddit")
                .addAfter("submission3")
                .addAfterRequest(null)
                .addLimit(Paginator.RECOMMENDED_MAX_LIMIT)
                .addSort("new")
                .build());
        adapter.addResponse(new OfflineSubmissionListingResponse()
                .addSubreddit("subreddit")
                .addAfterRequest("submission3")
                .addLimit(Paginator.RECOMMENDED_MAX_LIMIT)
                .addSort("new")
                .build());
        
        instance.actions.clear();
        instance.messages.clear();
    }
    @Test
    public void addSubredditsTest(){
        assertTrue(feed.posts.isEmpty());
        feed.addSubreddits(server,instance.guild);
        assertEquals(feed.posts.size(),4);
        assertTrue(feed.posts.containsEntry("subreddit", instance.channel1));
        assertTrue(feed.posts.containsEntry("subreddit", instance.channel2));
        assertTrue(feed.posts.containsEntry("stuff", instance.channel2));
        assertTrue(feed.posts.containsEntry("stuff", instance.channel3));
        assertEquals(feed.history.size(),2);
        assertTrue(feed.history.containsKey("subreddit"));
        assertTrue(feed.history.containsKey("stuff"));
    }
    @Test
    public void addFeedTest(){
        assertTrue(feed.posts.isEmpty());
        assertTrue(feed.history.isEmpty());
        feed.addFeed("subreddit", instance.channel1);
        assertTrue(feed.posts.containsEntry("subreddit", instance.channel1));
        assertTrue(feed.history.containsKey("subreddit"));
    }
    @Test
    public void removeFeedTest(){
        feed.addFeed("subreddit", instance.channel1);
        assertTrue(feed.posts.containsEntry("subreddit", instance.channel1));
        assertTrue(feed.history.containsKey("subreddit"));
        feed.removeFeed("subreddit", instance.channel1);
        assertTrue(feed.posts.isEmpty());
        assertTrue(feed.history.isEmpty());
    }
    @Test
    public void removeFeedButNotHistoryTest(){
        feed.addFeed("subreddit", instance.channel1);
        feed.addFeed("subreddit", new TextChannelImpl(100,instance.guild));
        assertTrue(feed.posts.containsEntry("subreddit", instance.channel1));
        assertTrue(feed.history.containsKey("subreddit"));
        feed.removeFeed("subreddit", instance.channel1);
        assertFalse(feed.posts.containsEntry("subreddit", instance.channel1));
        assertTrue(feed.history.containsKey("subreddit"));
    }
    @Test
    public void requestSubmissionFirstTimeTest(){
        feed.history.clear();
        List<Submission> list = feed.requestSubmissions("subreddit");
        assertTrue(list.isEmpty());
    }
    @Test
    public void requestSubmissionTest(){
        feed.history.put("subreddit", 0L);
        List<Submission> list = feed.requestSubmissions("subreddit");
        assertEquals(list.size(),3);
        assertEquals(list.get(0).getTitle(),"title1");
        assertEquals(list.get(1).getTitle(),"title2");
        assertEquals(list.get(2).getTitle(),"title3");
    }
    @Test
    public void requestSubmissionNotFoundTest() throws InterruptedException{
        feed.addFeed("unknown", instance.channel1);
        assertTrue(feed.posts.containsEntry("unknown", instance.channel1));
        assertTrue(feed.requestSubmissions("unknown").isEmpty());
        feed.error_handler.shutdown();
        feed.error_handler.awaitTermination(20, TimeUnit.SECONDS);
        assertFalse(feed.posts.containsEntry("unknown", instance.channel1));
    }
    @Test
    public void requestSubmissionForbiddenTest() throws InterruptedException{
        feed.addFeed("forbidden", instance.channel1);
        adapter.addError(new OfflineSubmissionListingResponse().addSubreddit("forbidden").addLimit(Paginator.RECOMMENDED_MAX_LIMIT).addSort("new").build().getRequest(), HttpStatus.SC_FORBIDDEN);
        assertTrue(feed.posts.containsEntry("forbidden", instance.channel1));
        assertTrue(feed.requestSubmissions("forbidden").isEmpty());
        feed.error_handler.shutdown();
        feed.error_handler.awaitTermination(20, TimeUnit.SECONDS);
        assertFalse(feed.posts.containsEntry("forbidden", instance.channel1));
    }
    @Test
    public void requestSubmissionOtherErrorTest() throws InterruptedException{
        feed.addFeed("other", instance.channel1);
        adapter.addError(new OfflineSubmissionListingResponse().addSubreddit("other").addLimit(Paginator.RECOMMENDED_MAX_LIMIT).addSort("new").build().getRequest(), 418);
        assertTrue(feed.posts.containsEntry("other", instance.channel1));
        assertTrue(feed.requestSubmissions("other").isEmpty());
        feed.error_handler.shutdown();
        feed.error_handler.awaitTermination(20, TimeUnit.SECONDS);
        assertTrue(feed.posts.containsEntry("other", instance.channel1));
    }
    @Test
    public void generateMessagesEmptyTest(){
        feed.history.put("empty", 5L);
        assertEquals(feed.history.get("empty").longValue(),5);
        Map<Submission,MessageBuilder> messages = feed.generateMessages("empty");
        assertTrue(messages.isEmpty());
        assertEquals(feed.history.get("empty").longValue(),5);
    }
    @Test
    public void generateMessagesTest(){
        feed.history.put("subreddit", 0L);
        Map<Submission,MessageBuilder> messages = feed.generateMessages("subreddit");
        assertEquals(messages.size(),3);
        assertEquals(messages.keySet().stream().map(Submission::getTitle).collect(Collectors.toSet()),Sets.newHashSet("title1","title2","title3"));
    }
    @Test(expected=IllegalStateException.class)
    public void generateMessagesDuplicatesTest(){
        OfflineSubmission sub = new OfflineSubmission()
                .addNumComments(10)
                .addThumbnailHeight(10)
                .addThumbnailWidth(10)
                .addThumbnail("https://www.reddit.com/thumbnail1.jpg")
                .addSelftext("selftext1")
                .addOver18(true)
                .addSpoilerTest(true)
                .addLikes()
                .addUrl("https://www.reddit.com/sub1.jpg")
                .addTitle("title1")
                .addSubredditId("subreddit_id")
                .addSubreddit("duplicate")
                .addPermalink("permalink1")
                .addId("id1")
                .addName("submission1")
                .addAuthor("author1")
                .addDomain("i.redd.it")
                .addDistinguished()
                .addCreatedUtc(System.currentTimeMillis()/1000-400);
        
        adapter.addResponse(new OfflineSubmissionListingResponse()
                .addSubmission(sub)
                .addSubmission(sub)
                .addSubreddit("duplicate")
                .addAfterRequest(null)
                .addAfter("submission1")
                .addLimit(Paginator.RECOMMENDED_MAX_LIMIT)
                .addSort("new")
                .build());
        adapter.addResponse(new OfflineSubmissionListingResponse()
                .addSubreddit("duplicate")
                .addAfterRequest("submission1")
                .addAfter("null")
                .addLimit(Paginator.RECOMMENDED_MAX_LIMIT)
                .addSort("new")
                .build());
        feed.history.put("duplicate", 0L);
        feed.generateMessages("duplicate");
    }
    
    @Test
    public void runTest() throws IOException, InterruptedException{
        feed.addSubreddits(server,instance.guild);
        feed.history.put("subreddit", 0L);
        assertTrue(instance.messages.isEmpty());
        feed.run();
        assertTrue(instance.messages.get(0).getContentRaw().contains("New submission from"));
        assertFalse(instance.messages.get(0).getEmbeds().isEmpty());
    }
    @Test
    public void runInvalidTextchannelTest() throws IOException, InterruptedException{
        assertFalse(instance.actions.contains("Guild "+instance.guild.getId()+" updated"));
        List<Runnable> list = new ArrayList<>();
        instance.guild.getTextChannelsMap().put(instance.channel1.getIdLong(), new TextChannelImpl(instance.channel1.getIdLong(),instance.guild){
            @Override
            public MessageAction sendMessage(Message message){
                return new MessageAction(instance.jda,null,instance.channel1){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){
                        list.add(() -> failure.accept(ErrorResponseException.create(ErrorResponse.UNKNOWN_CHANNEL, new FakeResponse())));
                    }
                };
            }
            @Override
            public boolean isNSFW(){
                return true;
            }
        });
        feed.addSubreddits(server,instance.guild);
        feed.history.put("subreddit", 0L);
        feed.run();
        assertEquals(list.size(),3);
        list.get(0).run();
        assertEquals(feed.history.size(),2);
        assertEquals(feed.posts.size(),3);
        assertFalse(feed.posts.containsKey(instance.channel1));
        assertFalse(server.getRedditFeeds(instance.guild).containsKey(instance.channel1));
        assertTrue(instance.actions.contains("Guild "+instance.guild.getId()+" updated"));
    }
    @Test
    public void runInvalidGuildTest() throws IOException, InterruptedException{
        assertFalse(instance.actions.contains("Guild "+instance.guild.getId()+" updated"));
        List<Runnable> list = new ArrayList<>();
        instance.guild.getTextChannelsMap().put(instance.channel1.getIdLong(), new TextChannelImpl(instance.channel1.getIdLong(),instance.guild){
            @Override
            public MessageAction sendMessage(Message message){
                return new MessageAction(instance.jda,null,instance.channel1){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){
                        list.add(() -> failure.accept(ErrorResponseException.create(ErrorResponse.UNKNOWN_GUILD, new FakeResponse())));
                    }
                };
            }
            @Override
            public boolean isNSFW(){
                return true;
            }
        });
        feed.addSubreddits(server,instance.guild);
        feed.history.put("subreddit", 0L);
        instance.guild.getMembersMap().remove(instance.self.getIdLong());
        feed.run();
        assertEquals(list.size(),3);
        list.get(0).run();
        assertEquals(feed.history.size(),2);
        assertEquals(feed.posts.size(),3);
        assertFalse(feed.posts.containsKey(instance.channel1));
        assertFalse(server.getRedditFeeds(instance.guild).containsKey(instance.channel1));
        assertTrue(instance.actions.contains("Guild "+instance.guild.getId()+" updated"));
    }
    @Test
    public void runUnknownDiscordErrorTest() throws IOException, InterruptedException{
        List<Runnable> list = new ArrayList<>();
        instance.guild.getTextChannelsMap().put(instance.channel1.getIdLong(), new TextChannelImpl(instance.channel1.getIdLong(),instance.guild){
            @Override
            public MessageAction sendMessage(Message message){
                return new MessageAction(instance.jda,null,instance.channel1){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){
                        list.add(() -> failure.accept(ErrorResponseException.create(ErrorResponse.BOTS_NOT_ALLOWED, new FakeResponse())));
                    }
                };
            }
            @Override
            public boolean isNSFW(){
                return true;
            }
        });
        feed.addSubreddits(server,instance.guild);
        feed.history.put("subreddit", 0L);
        instance.guild.getMembersMap().remove(instance.self.getIdLong());
        feed.run();
        assertEquals(list.size(),3);
        list.get(0).run();
        assertEquals(feed.history.size(),2);
        assertEquals(feed.posts.size(),4);
    }
    @Test
    public void runUnknownErrorTest() throws IOException, InterruptedException{
        List<Runnable> list = new ArrayList<>();
        instance.guild.getTextChannelsMap().put(instance.channel1.getIdLong(), new TextChannelImpl(instance.channel1.getIdLong(),instance.guild){
            @Override
            public MessageAction sendMessage(Message message){
                return new MessageAction(instance.jda,null,instance.channel1){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){
                        list.add(() -> failure.accept(new Exception()));
                    }
                };
            }
            @Override
            public boolean isNSFW(){
                return true;
            }
        });
        feed.addSubreddits(server,instance.guild);
        feed.history.put("subreddit", 0L);
        instance.guild.getMembersMap().remove(instance.self.getIdLong());
        feed.run();
        assertEquals(list.size(),3);
        list.get(0).run();
        assertEquals(feed.history.size(),2);
        assertEquals(feed.posts.size(),4);
    }
    @Test
    public void runInsufficientPermissionTest() throws InterruptedException{
        assertFalse(instance.actions.contains("Guild "+instance.guild.getId()+" updated"));
        instance.guild.setOwner(instance.member);
        instance.guild.getTextChannelsMap().put(instance.channel1.getIdLong(), new TextChannelImpl(instance.channel1.getIdLong(),instance.guild){
            @Override
            public boolean isNSFW(){
                return true;
            }
        });
        feed.addSubreddits(server,instance.guild);
        feed.history.put("subreddit", 0L);
        instance.public_role.setRawPermissions(0);
        feed.run();
        feed.error_handler.shutdown();
        feed.error_handler.awaitTermination(20, TimeUnit.SECONDS);
        assertEquals(feed.history.size(),2);
        assertEquals(feed.posts.size(),3);
        assertFalse(feed.posts.containsKey(instance.channel1));
        assertFalse(server.getRedditFeeds(instance.guild).containsKey(instance.channel1));
        assertTrue(instance.actions.contains("Guild "+instance.guild.getId()+" updated"));
    }
    @Test
    public void runChannelIsNotNsfwTest() throws IOException, InterruptedException{
        instance.channel1.setNSFW(false);
        feed.addSubreddits(server,instance.guild);
        feed.history.put("subreddit", 0L);
        assertTrue(instance.messages.isEmpty());
        feed.run();
        assertTrue(instance.messages.size()>0);
    }
    
    @Test
    public void runSubmissionIsNsfwTest() throws IOException, InterruptedException{
        feed.addSubreddits(server,instance.guild);
        feed.history.put("subreddit", 0L);
        assertTrue(instance.messages.isEmpty());
        feed.run();
        assertEquals(instance.messages.size(),6);
        instance.messages.clear();
        
        instance.channel1.setNSFW(false);
        feed.addSubreddits(server,instance.guild);
        feed.history.put("subreddit", 0L);
        assertTrue(instance.messages.isEmpty());
        feed.run();
        assertEquals(instance.messages.size(),5);
    }
    
    @Test
    public void runNoSubmissionTest() throws InterruptedException{
        feed.addSubreddits(server,instance.guild);
        feed.history.put("subreddit", System.currentTimeMillis()+1000);
        assertTrue(instance.messages.isEmpty());
        feed.run();
        assertTrue(instance.messages.isEmpty());
    }
    @Test
    public void runUnexpectedErrorTest(){
        feed = new RedditFeed(bot, (g) -> instance.bot){
            @Override
            public synchronized List<Submission> requestSubmissions(String subreddit){
                throw new RuntimeException();
            }
        };
        feed.addSubreddits(server,instance.guild);
        feed.run();
    }
    
    private class FakeResponse extends Response{
        public FakeResponse(){
            super(null, new Exception(), new HashSet<>());
        }
    }
}