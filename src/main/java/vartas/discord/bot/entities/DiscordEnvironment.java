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

package vartas.discord.bot.entities;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import vartas.discord.bot.StatusTracker;
import vartas.discord.bot.reddit.RedditFeed;
import vartas.discord.bot.visitor.DiscordEnvironmentVisitor;
import vartas.reddit.CommentInterface;
import vartas.reddit.SubmissionInterface;
import vartas.reddit.SubredditInterface;
import vartas.reddit.UnresolvableRequestException;
import vartas.reddit.jraw.JrawClient;
import vartas.reddit.pushshift.PushshiftClient;

import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiscordEnvironment {
    private ScheduledExecutorService global = Executors.newScheduledThreadPool(2);
    private List<DiscordCommunicator> communicators = new ArrayList<>();
    private BotRank rank;
    private BotConfig config;
    private JrawClient reddit;
    private PushshiftClient pushshift;
    private RedditFeed feed;

    public DiscordEnvironment(BotRank rank, BotConfig config, BotStatus status, Function<DiscordCommunicator, CommandBuilder> builder, Function<Guild, BotGuild> guilds) throws LoginException, InterruptedException {
        this.rank = rank;
        this.config = config;
        this.reddit = new JrawClient(config.getRedditAccount(), this.getClass().getPackage().getImplementationVersion(), config.getRedditId(), config.getRedditSecret());
        this.pushshift = new PushshiftClient(reddit);
        this.feed = new RedditFeed(this);

        addCommunicators(builder, guilds);
        removeOldGuilds();

        this.reddit.login();

        schedule(feed, 1, TimeUnit.MINUTES);
        schedule(new StatusTracker(this, status), config.getStatusMessageUpdateInterval(), TimeUnit.MINUTES);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Initialization                                                                                               //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addCommunicators(Function<DiscordCommunicator, CommandBuilder> builder, Function<Guild, BotGuild> guilds) throws LoginException, InterruptedException {
        for(int i = 0 ; i < config().getDiscordShards() ; ++i){
            communicators.add(new DiscordCommunicator(this, createJDA(i), builder, guilds));
            //We are only allowed to connect one shard every 5 seconds.
            if(i < config.getDiscordShards() - 1)
                Thread.sleep(5000);
        }
    }

    private JDA createJDA(int shard) throws LoginException, InterruptedException{
        return new JDABuilder()
                .setStatus(OnlineStatus.ONLINE)
                .setToken(config().getDiscordToken())
                .setAutoReconnect(true)
                .useSharding(shard, (int)config().getDiscordShards())
                .build()
                .awaitStatus(JDA.Status.CONNECTED);
    }
    private void removeOldGuilds(){

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Configuration Instances                                                                                      //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public BotRank rank(){
        return rank;
    }
    public BotConfig config(){
        return config;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Discord                                                                                                      //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @return a list of the JDA instances responsible for the individual shards.
     */
    public List<JDA> jdas(){
        return communicators.stream().map(DiscordCommunicator::jda).collect(Collectors.toList());
    }
    /**
     * @return a collection of all guilds in the individual shards.
     */
    public List<Guild> guilds(){
        return jdas().stream().map(JDA::getGuilds).flatMap(Collection::stream).collect(Collectors.toList());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Threads                                                                                                      //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Runnable shutdown(){
        return () -> communicators.stream().map(DiscordCommunicator::shutdown).forEach(Runnable::run);
    }
    public void schedule(Runnable runnable){
        global.submit(runnable);
    }
    protected void schedule(Runnable runnable, long period, TimeUnit unit){
        global.scheduleAtFixedRate(runnable, 0, period, unit);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Reddit                                                                                                       //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Optional<SubredditInterface> subreddit(String subreddit) throws UnresolvableRequestException {
        return reddit.requestSubreddit(subreddit);
    }
    public Optional<List<CommentInterface>> comment(SubmissionInterface submission) throws UnresolvableRequestException{
        return reddit.requestComment(submission.getId());
    }
    public Optional<TreeSet<SubmissionInterface>> pushshift(String subreddit, Instant start, Instant end) throws UnresolvableRequestException {
        return pushshift.requestSubmission(subreddit, Date.from(start), Date.from(end));
    }
    public Optional<TreeSet<SubmissionInterface>> submission(String subreddit, Instant start, Instant end) throws UnresolvableRequestException {
        return reddit.requestSubmission(subreddit, Date.from(start), Date.from(end));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Communicator                                                                                                 //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public DiscordCommunicator communicator(Guild guild){
        return communicator(guild.getId());
    }
    public DiscordCommunicator communicator(TextChannel channel){
        return communicator(channel.getGuild());
    }
    public DiscordCommunicator communicator(Role role){
        return communicator(role.getGuild());
    }
    private DiscordCommunicator communicator(String id) throws NumberFormatException{
        return communicator(Long.parseLong(id));
    }
    private DiscordCommunicator communicator(long id){
        int shard = (int)((id >> 22) % config().getDiscordShards());
        return communicators.get(shard);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Visitor                                                                                                      //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void accept(DiscordEnvironmentVisitor visitor){
        communicators.forEach(visitor::handle);
        visitor.handle(feed);
    }
}
