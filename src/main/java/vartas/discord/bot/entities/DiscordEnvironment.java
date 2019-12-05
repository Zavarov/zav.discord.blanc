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
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.apache.http.client.HttpResponseException;
import org.slf4j.Logger;
import vartas.discord.bot.CommandBuilder;
import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.StatusTracker;
import vartas.discord.bot.reddit.RedditFeed;
import vartas.discord.bot.visitor.DiscordEnvironmentVisitor;
import vartas.reddit.Comment;
import vartas.reddit.Submission;
import vartas.reddit.Subreddit;
import vartas.reddit.jraw.JrawClient;
import vartas.reddit.pushshift.PushshiftClient;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiscordEnvironment {
    /**
     * The maximum amount of times a Reddit request will be repeated when unsuccessful until we give up.
     */
    private static final int MAX_RETRIES = 7;
    /**
     * The logger for the communicator.
     */
    protected Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    private ScheduledExecutorService global = Executors.newScheduledThreadPool(2);
    private List<DiscordCommunicator> communicators = new ArrayList<>();
    private BotRank rank;
    private Credentials config;
    private JrawClient reddit;
    private PushshiftClient pushshift;
    private RedditFeed feed;
    private EntityAdapter adapter;
    private StatusTracker status;

    protected DiscordEnvironment(EntityAdapter adapter){
        this.adapter = adapter;
        this.config = adapter.config();
        this.rank = adapter.rank();

        String account = config.getRedditAccount();
        String version = Optional.ofNullable(this.getClass().getPackage().getImplementationVersion()).orElse("debug");
        String id = config.getRedditId();
        String secret = config.getRedditSecret();

        this.reddit = new JrawClient(account, version, id, secret);
        this.pushshift = new PushshiftClient(reddit);
        this.feed = new RedditFeed(this);
        this.status = new StatusTracker(this, adapter.status());
    }

    public DiscordEnvironment(EntityAdapter adapter, Function<DiscordCommunicator, CommandBuilder> builder) throws LoginException, InterruptedException {
        this(adapter);

        reddit.login();

        addCommunicators(builder);
        removeOldGuilds();

        schedule(feed, 1, TimeUnit.MINUTES);
        schedule(status, config.getStatusMessageUpdateInterval(), TimeUnit.MINUTES);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Initialization                                                                                               //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addCommunicators(Function<DiscordCommunicator, CommandBuilder> builder) throws LoginException, InterruptedException {
        for(int i = 0 ; i < config().getDiscordShards() ; ++i){
            communicators.add(new DiscordCommunicator(this, createJDA(i), builder, adapter));
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
                .useSharding(shard, config().getDiscordShards())
                .build()
                .awaitStatus(JDA.Status.CONNECTED);
    }
    private void removeOldGuilds(){
        Path guildPath = Paths.get("guilds");
        Set<String> ids = guilds().stream().map(ISnowflake::getId).collect(Collectors.toSet());

        try {
            if(Files.notExists(guildPath))
                return;

            Files.newDirectoryStream(guildPath).forEach(file -> {
                Path fileName = file.getFileName();
                String name = fileName.toString().substring(0, fileName.toString().lastIndexOf('.'));

                if(!ids.contains(name)){
                    try{
                        Files.deleteIfExists(file);
                    }catch(IOException e){
                        log.error(e.getMessage(), e);
                    }
                }
            });
        }catch(IOException e){
            log.error(e.getMessage(), e);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Configuration Instances                                                                                      //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public BotRank rank(){
        return rank;
    }
    public Credentials config(){
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
    public Optional<Subreddit> subreddit(String subreddit) {
        try {
            return reddit.requestSubreddit(subreddit, MAX_RETRIES);
        }catch(HttpResponseException e){
            throw new IllegalArgumentException(e);
        }
    }
    public Optional<? extends Collection<Comment>> comment(Submission submission) {
        try {
            return reddit.requestComment(submission.getId(), MAX_RETRIES);
        }catch(HttpResponseException e){
            throw new IllegalArgumentException(e);
        }
    }
    public Optional<? extends Collection<Submission>> pushshift(String subreddit, LocalDateTime start, LocalDateTime end) {
        try {
            return pushshift.requestSubmission(subreddit, start, end, MAX_RETRIES);
        }catch(HttpResponseException e){
            throw new IllegalArgumentException(e);
        }
    }
    public Optional<? extends Collection<Submission>> submission(String subreddit, LocalDateTime start, LocalDateTime end) {
        try {
            return reddit.requestSubmission(subreddit, start, end, MAX_RETRIES);
        }catch(HttpResponseException e){
            throw new IllegalArgumentException(e);
        }
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
        visitor.handle(rank);
    }
}
