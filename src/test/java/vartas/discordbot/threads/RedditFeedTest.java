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
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.dean.jraw.http.HttpResponse;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Submission;
import net.dean.jraw.pagination.Paginator;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.SelfUserImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.requests.ErrorResponse;
import net.dv8tion.jda.core.requests.Response;
import net.dv8tion.jda.core.requests.RestAction;
import okhttp3.Protocol;
import org.apache.http.HttpStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.discordbot.comm.AbstractCommunicator;
import vartas.discordbot.comm.Communicator;
import vartas.discordbot.comm.Environment;
import vartas.discordbot.comm.OfflineCommunicator;
import vartas.discordbot.comm.OfflineEnvironment;
import vartas.discordbot.threads.RedditFeed.ErrorHandling;
import vartas.offlinejraw.OfflineNetworkAdapter;
import vartas.offlinejraw.OfflineSubmissionListingResponse;
import vartas.offlinejraw.OfflineSubmissionListingResponse.OfflineSubmission;
import vartas.offlinejraw.OfflineSubredditResponse;
import vartas.xml.XMLPermission;
import vartas.xml.XMLServer;

/**
 *
 * @author u/Zavarov
 */
public class RedditFeedTest {
    static OfflineCommunicator comm;
    static OfflineNetworkAdapter adapter;
    static JDAImpl jda;
    GuildImpl guild;
    TextChannelImpl channel1;
    TextChannelImpl channel2;
    TextChannelImpl channel3;
    TextChannelImpl  channel4;
    RedditFeed feed;
    SelfUserImpl self;
    MemberImpl memberself;
    XMLServer server;
    long now;
    @BeforeClass
    public static void startUp(){
        comm = (OfflineCommunicator)new OfflineEnvironment().comm(0);
        jda = (JDAImpl)comm.jda();
        adapter = (OfflineNetworkAdapter)comm.environment().adapter();
        
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
    }
    
    @Before
    public void setUp(){
        now = System.currentTimeMillis();
        feed = new RedditFeed(comm.environment());
        server = XMLServer.create(new File("src/test/resources/guilds/0.server"));
        
        guild = new GuildImpl(jda , 0);
        channel1 = new TextChannelImpl(1, guild);
        channel2 = new TextChannelImpl(2, guild);
        channel3 = new TextChannelImpl(3, guild);
        channel4 = new TextChannelImpl(4, guild);
        guild.getTextChannelsMap().put(channel1.getIdLong(), channel1);
        guild.getTextChannelsMap().put(channel2.getIdLong(), channel2);
        guild.getTextChannelsMap().put(channel3.getIdLong(), channel3);
        guild.getTextChannelsMap().put(channel4.getIdLong(), channel4);
        self = new SelfUserImpl(0L,jda);
        memberself = new MemberImpl(guild, self);
        
        jda.setSelfUser(self);
        jda.getUserMap().put(self.getIdLong(),self);
        guild.getMembersMap().put(self.getIdLong(),memberself);
        guild.setOwner(memberself);
        channel1.setNSFW(true);
        channel2.setNSFW(true);
        channel3.setNSFW(true);
        channel4.setNSFW(true);
        
        channel1.setName("channel1");
        channel2.setName("channel2");
        channel3.setName("channel3");
        channel4.setName("channel4");
        guild.setName("guild");
        
        comm.actions.clear();
        comm.discord.clear();
    }
    @Test
    public void addSubredditsTest(){
        assertTrue(feed.posts.isEmpty());
        feed.addSubreddits(server,guild);
        assertEquals(feed.posts.size(),4);
        assertTrue(feed.posts.containsEntry("subreddit", channel1));
        assertTrue(feed.posts.containsEntry("subreddit", channel2));
        assertTrue(feed.posts.containsEntry("stuff", channel2));
        assertTrue(feed.posts.containsEntry("stuff", channel3));
        assertEquals(feed.history.size(),2);
        assertTrue(feed.history.containsKey("subreddit"));
        assertTrue(feed.history.containsKey("stuff"));
    }
    @Test
    public void addFeedTest(){
        assertTrue(feed.posts.isEmpty());
        assertTrue(feed.history.isEmpty());
        feed.addFeed("subreddit", channel1);
        assertTrue(feed.posts.containsEntry("subreddit", channel1));
        assertTrue(feed.history.containsKey("subreddit"));
    }
    @Test
    public void removeFeedTest() throws IOException, InterruptedException{
        feed.addFeed("subreddit", channel1);
        assertTrue(feed.posts.containsEntry("subreddit", channel1));
        assertTrue(feed.history.containsKey("subreddit"));
        feed.removeFeed("subreddit", channel1);
        assertTrue(feed.posts.isEmpty());
        assertTrue(feed.history.isEmpty());
    }
    @Test
    public void removeFeedButNotHistoryTest() throws IOException, InterruptedException{
        feed.addFeed("subreddit", channel1);
        feed.addFeed("subreddit", channel4);
        assertTrue(feed.posts.containsEntry("subreddit", channel1));
        assertTrue(feed.history.containsKey("subreddit"));
        feed.removeFeed("subreddit", channel1);
        assertFalse(feed.posts.containsEntry("subreddit", channel1));
        assertTrue(feed.history.containsKey("subreddit"));
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
        
        startUp();
    }
    @Test
    public void generateMessagesUnknownSubredditTest(){
        Map<Submission,MessageBuilder> messages = feed.generateMessages("unknown");
        assertTrue(messages.isEmpty());
    }
    @Test
    public void generateMessagesNetworkExceptionTest(){
        okhttp3.Request request = new okhttp3.Request.Builder().url("http://www.test.con").build();
        okhttp3.Response response = new okhttp3.Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_2)
                .code(HttpStatus.SC_FORBIDDEN)
                .message("message").build();
        HttpResponse http = new HttpResponse(response);
        NetworkException exception = new NetworkException(http);
        
        List<Runnable> list = new ArrayList<>();
        
        OfflineCommunicator fake = new OfflineCommunicator(comm.environment(),comm.jda()){
            @Override
            public void submit(Runnable runnable){
                list.add(runnable);
            }
        };
        Environment environment = new OfflineEnvironment(){
            @Override
            public Communicator comm(Guild guild){
                return fake;
            }
            @Override
            public Communicator comm(TextChannel channel){
                return fake;
            }
            @Override
            public List<Submission> submission(String subreddit, Instant start, Instant end){
                throw exception;
            }
        };
        
        feed = new RedditFeed(environment);
        feed.addSubreddits(server, guild);
        
        feed.generateMessages("subreddit");
        list.forEach(Runnable::run);
            
        assertFalse(feed.posts.containsEntry("subreddit",channel1));
        assertFalse(feed.posts.containsEntry("subreddit",channel2));
        assertFalse(feed.history.containsKey("subreddit"));
        assertEquals(fake.actions,Arrays.asList(guild.getName()+" updated",guild.getName()+" updated"));
    }
    @Test
    public void generateMessagesHarmlessNetworkExceptionTest(){
        okhttp3.Request request = new okhttp3.Request.Builder().url("http://www.test.con").build();
        okhttp3.Response response = new okhttp3.Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_2)
                .code(HttpStatus.SC_OK)
                .message("message").build();
        HttpResponse http = new HttpResponse(response);
        NetworkException exception = new NetworkException(http);
        
        List<Runnable> list = new ArrayList<>();
        
        OfflineCommunicator fake = new OfflineCommunicator(comm.environment(),comm.jda()){
            @Override
            public void submit(Runnable runnable){
                list.add(runnable);
            }
        };
        Environment environment = new OfflineEnvironment(){
            @Override
            public Communicator comm(Guild guild){
                return fake;
            }
            @Override
            public Communicator comm(TextChannel channel){
                return fake;
            }
            @Override
            public List<Submission> submission(String subreddit, Instant start, Instant end){
                throw exception;
            }
        };
        
        feed = new RedditFeed(environment);
        feed.addSubreddits(server, guild);
        
        feed.generateMessages("subreddit");
        list.forEach(Runnable::run);
            
        assertTrue(feed.posts.containsEntry("subreddit",channel1));
        assertTrue(feed.posts.containsEntry("subreddit",channel2));
        assertTrue(feed.history.containsKey("subreddit"));
        assertEquals(fake.actions,Arrays.asList());
    }
    @Test
    public void runTest(){
        feed.addSubreddits(server,guild);
        feed.history.put("subreddit", 0L);
        assertTrue(comm.discord.entries().isEmpty());
        feed.run();
        assertTrue(comm.discord.get(channel1).get(0).getContentRaw().contains("New submission from"));
        assertFalse(comm.discord.get(channel1).get(0).getEmbeds().isEmpty());
    }
    @Test
    public void runChannelIsNotNsfwTest(){
        channel1.setNSFW(false);
        feed.addSubreddits(server,guild);
        feed.history.put("subreddit", 0L);
        assertTrue(comm.discord.entries().isEmpty());
        feed.run();
        assertFalse(comm.discord.entries().isEmpty());
    }
    
    @Test
    public void runSubmissionIsNsfwTest(){
        feed.addSubreddits(server,guild);
        feed.history.put("subreddit", 0L);
        assertTrue(comm.discord.isEmpty());
        feed.run();
        assertEquals(comm.discord.entries().size(),6);
        comm.discord.clear();
        
        channel1.setNSFW(false);
        feed.addSubreddits(server,guild);
        feed.history.put("subreddit", 0L);
        assertTrue(comm.discord.isEmpty());
        feed.run();
        assertEquals(comm.discord.size(),5);
    }
    
    @Test
    public void runNoSubmissionTest(){
        feed.addSubreddits(server,guild);
        feed.history.put("subreddit", System.currentTimeMillis()+1000);
        assertTrue(comm.discord.isEmpty());
        feed.run();
        assertTrue(comm.discord.isEmpty());
    }
    @Test
    public void runUnexpectedErrorTest(){
        feed = new RedditFeed(comm.environment()){
            @Override
            public synchronized Map<Submission,MessageBuilder> generateMessages(String subreddit){
                throw new RuntimeException();
            }
        };
        feed.addSubreddits(server, guild);
        feed.run();
    }
    @Test
    public void runInsufficientPermissionTest(){
        OfflineCommunicator fake = new OfflineCommunicator(comm.environment(),comm.jda()){
            @Override
            public void send(MessageChannel channel, MessageBuilder message, Consumer<Message> success, Consumer<Throwable> failure){
                throw new InsufficientPermissionException(Permission.ADMINISTRATOR);
            }
            @Override
            public void submit(Runnable runnable){
                runnable.run();
            }
        };
        Environment environment = new OfflineEnvironment(){
            @Override
            public Communicator comm(Guild guild){
                return fake;
            }
            @Override
            public Communicator comm(TextChannel channel){
                return fake;
            }
            @Override
            public List<Submission> submission(String subreddit, Instant start, Instant end){
                return comm.environment().submission(subreddit, start, end);
            }
        };
        
        feed = new RedditFeed(environment);
        feed.addSubreddits(server, guild);
        feed.history.put("subreddit", 0L);
        feed.run();
        
        assertFalse(feed.posts.containsValue(channel1));
        assertTrue(feed.history.containsKey("subreddit"));
        assertEquals(fake.actions,Arrays.asList(guild.getName()+" updated",guild.getName()+" updated",guild.getName()+" updated"));
    }
    @Test
    public void invalidTextchannelTest(){
        feed.addSubreddits(server, guild);
        
        ErrorHandling error = feed.new ErrorHandling("subreddit",channel1);
        error.accept(ErrorResponseException.create(ErrorResponse.UNKNOWN_CHANNEL, new FakeResponse()));
        
        assertFalse(feed.posts.containsValue(channel1));
        assertTrue(feed.history.containsKey("subreddit"));
        assertEquals(comm.actions,Arrays.asList(guild.getName()+" updated"));
    }
    @Test
    public void invalidGuildTest(){
        feed.addSubreddits(server, guild);
        
        ErrorHandling error = feed.new ErrorHandling("subreddit",channel1);
        error.accept(ErrorResponseException.create(ErrorResponse.UNKNOWN_GUILD, new FakeResponse()));
        
        assertFalse(feed.posts.containsValue(channel1));
        assertTrue(feed.history.containsKey("subreddit"));
        assertEquals(comm.actions,Arrays.asList(guild.getName()+" updated"));
    }
    @Test
    public void unknownDiscordErrorTest(){
        feed.addSubreddits(server, guild);
        
        ErrorHandling error = feed.new ErrorHandling("subreddit",channel1);
        error.accept(ErrorResponseException.create(ErrorResponse.BOTS_NOT_ALLOWED, new FakeResponse()));
        
        assertTrue(feed.posts.containsValue(channel1));
        assertTrue(feed.history.containsKey("subreddit"));
        assertTrue(comm.actions.isEmpty());
    }
    @Test
    public void insufficientPermissionTest(){
        feed.addSubreddits(server, guild);
        
        ErrorHandling error = feed.new ErrorHandling("subreddit",channel1);
        error.accept(new InsufficientPermissionException(Permission.ADMINISTRATOR));
        
        assertFalse(feed.posts.containsValue(channel1));
        assertTrue(feed.history.containsKey("subreddit"));
        assertEquals(comm.actions,Arrays.asList(guild.getName()+" updated"));
    }
    @Test
    public void networkExceptionTest(){
        feed.addSubreddits(server, guild);
        
        ErrorHandling error = feed.new ErrorHandling("subreddit",channel1);
        error.accept(new NetworkException(new OfflineSubredditResponse().build()));
        
        assertFalse(feed.posts.containsValue(channel1));
        assertTrue(feed.history.containsKey("subreddit"));
        assertEquals(comm.actions,Arrays.asList(guild.getName()+" updated"));
    }
    @Test
    public void ioexceptionTest(){
        AbstractCommunicator fake = new AbstractCommunicator(comm.environment(),comm.jda()){
            @Override
            public void update(Guild guild) throws IOException{
                comm.actions.add("error");
                throw new IOException();
            }
            @Override
            public void send(MessageChannel channel, MessageBuilder message, Consumer<Message> success, Consumer<Throwable> failure) {
            }
            @Override
            public <T> void send(RestAction<T> action, Consumer<T> success, Consumer<Throwable> failure) {
            }
            @Override
            public void delete(MessageChannel channel, long id) {
            }
            @Override
            public void delete(Guild guild) {
            }
            @Override
            public void update(XMLPermission permission) throws IOException, InterruptedException {
            }
        };
        Environment environment = new OfflineEnvironment(){
            @Override
            public Communicator comm(Guild guild){
                return fake;
            }
            @Override
            public Communicator comm(TextChannel channel){
                return fake;
            }
            @Override
            public List<Submission> submission(String subreddit, Instant start, Instant end){
                return comm.environment().submission(subreddit, start, end);
            }
        };
        feed = new RedditFeed(environment);
        feed.addSubreddits(server, guild);
        
        ErrorHandling error = feed.new ErrorHandling("subreddit",channel1);
        error.accept(new NetworkException(new OfflineSubredditResponse().build()));
        
        assertFalse(feed.posts.containsValue(channel1));
        assertTrue(feed.history.containsKey("subreddit"));
        assertEquals(comm.actions,Arrays.asList("error"));
    }
    @Test
    public void unknownExceptionTest(){
        feed.addSubreddits(server, guild);
        ErrorHandling error = feed.new ErrorHandling("subreddit",channel1);
        error.accept(new Exception());
        
        assertTrue(feed.posts.containsValue(channel1));
        assertTrue(feed.history.containsKey("subreddit"));
        assertTrue(comm.actions.isEmpty());
    }
    @Test
    public void handlerRunTest(){
        feed.addSubreddits(server, guild);
        ErrorHandling error = feed.new ErrorHandling("subreddit",channel1,new NetworkException(new OfflineSubredditResponse().build()));
        error.run();
        
        assertFalse(feed.posts.containsValue(channel1));
        assertTrue(feed.history.containsKey("subreddit"));
        assertEquals(comm.actions,Arrays.asList(guild.getName()+" updated"));
    }
    
    private class FakeResponse extends Response{
        public FakeResponse(){
            super(null, new Exception(), new HashSet<>());
        }
    }
}