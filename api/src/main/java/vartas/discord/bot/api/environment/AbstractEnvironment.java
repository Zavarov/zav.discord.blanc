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
package vartas.discord.bot.api.environment;

import de.monticore.symboltable.GlobalScope;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import vartas.discord.bot.api.command.AbstractCommandBuilder;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.api.threads.RedditFeed;
import vartas.discord.bot.api.threads.StatusTracker;
import vartas.discord.bot.io.config._ast.ASTConfigArtifact;
import vartas.discord.bot.io.rank.RankConfiguration;
import vartas.discord.bot.io.rank.RankHelper;
import vartas.reddit.CommentInterface;
import vartas.reddit.SubmissionInterface;
import vartas.reddit.SubredditInterface;
import vartas.reddit.UnresolvableRequestException;
import vartas.reddit.jraw.JrawClient;
import vartas.reddit.pushshift.PushshiftClient;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class implements the communication with the Reddit API and the crawler.<br>
 * Additionally, it also implements the XML files, with the exception of the
 * config file.
 */
public abstract class AbstractEnvironment implements EnvironmentInterface {
    /**
     * The underlying scope that contains
     */
    protected GlobalScope commands;
    /**
     * The builder for generating the commands from the calls.
     */
    protected Function<CommunicatorInterface, AbstractCommandBuilder> builder;
    /**
     * The formatter for dates.
     */
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
    /**
     * All bots indexed by their shard.
     */
    protected List<CommunicatorInterface> shards;
    /**
     * The instance that is used to crawl through the Reddit submissions.
     */
    protected PushshiftClient pushshift;
    /**
     * The instance that communicates with the Reddit API.
     */
    protected JrawClient reddit;
    /**
     * The feed that checks for new submissions.
     */
    protected RedditFeed feed;
    /**
     * The list of users with special ranks.
     */
    protected RankConfiguration permission;
    /**
     * The configuration file.
     */
    protected ASTConfigArtifact config;
    /**
     * The tracker that updates the game.
     */
    protected StatusTracker tracker;
    /**
     * Loads the remaining XML files and initializes the Reddit communicators. 
     * @param config the configuration file.
     * @param commands the scope for all valid commands
     * @param builder the builder for generating the commands from the calls
     */
    protected AbstractEnvironment(ASTConfigArtifact config, GlobalScope commands, Function<CommunicatorInterface, AbstractCommandBuilder> builder){
        this.config = config;
        this.commands = commands;
        this.builder = builder;

        this.permission = RankHelper.parse("rank.perm", new File("rank.perm"));

        this.shards = new ArrayList<>(config.getDiscordShards());
        this.reddit = new JrawClient(config.getRedditAccount(), this.getClass().getPackage().getImplementationVersion(), config.getRedditId(), config.getRedditSecret());
        this.pushshift = new PushshiftClient(reddit);

        this.reddit.login();

        this.feed = new RedditFeed(this);
        this.tracker = new StatusTracker(this);

        this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    /**
     * @return config file.
     */
    @Override
    public ASTConfigArtifact config(){
        return config;
    }
    /**
     * Requests the submissions and comments in the subreddit within the
     * given interval via the pushshift crawler.
     * @param subreddit the name of the
     * @param start the inclusive time stamp of the oldest submission.
     * @param end the inclusive time stamp of the newest submission.
     * @throws UnresolvableRequestException if an error occured during the request that couldn't be handled.
     */
    @Override
    public Optional<TreeSet<SubmissionInterface>> pushshift(String subreddit, Instant start, Instant end) throws UnresolvableRequestException {
        return pushshift.requestSubmission(subreddit, Date.from(start), Date.from(end));
    }
    /**
     * @param subreddit the subreddit the submissions are from.
     * @param start the inclusively oldest submission in the interval.
     * @param end the inclusively newest submission in the interval.
     * @return the submissions in the subreddit within the given interval.
     * @throws UnresolvableRequestException if an error occured during the request that couldn't be handled.
     */
    @Override
    public Optional<TreeSet<SubmissionInterface>> submission(String subreddit, Instant start, Instant end) throws UnresolvableRequestException {
        return reddit.requestSubmission(subreddit, Date.from(start), Date.from(end));
    }
    /**
     * @param subreddit the name of the subreddit.
     * @return the subreddit instance with that name.
     * @throws UnresolvableRequestException if an error occured during the request that couldn't be handled.
     */
    @Override
    public Optional<SubredditInterface> subreddit(String subreddit) throws UnresolvableRequestException {
        return reddit.requestSubreddit(subreddit);
    }
    /**
     * @param id the id of the guild.
     * @return the bot that this guild belongs to. 
     */
    @Override
    public CommunicatorInterface communicator(long id){
        int shard = (int)((id >> 22) % config().getDiscordShards());
        return shards.get(shard);
    }
    /**
     * Makes the program post submissions from the subreddit in the specified channel.
     * @param subreddit the name of the subreddit.
     * @param channel the textchannel where new submissions are posted.
     */
    @Override
    public void add(String subreddit, TextChannel channel){
        this.communicator(channel).config(channel).addRedditFeed(subreddit, channel);
        feed.addFeed(subreddit, channel);
    }
    /**
     * Removes a channel from the set of all channels where new submissions from
     * this subreddit are posted.
     * @param subreddit the name of the subreddit.
     * @param channel the channel that is removed from the set.
     */
    @Override
    public void remove(String subreddit, TextChannel channel){
        this.communicator(channel).config(channel).removeRedditFeed(subreddit, channel);
        feed.removeFeed(subreddit, channel);
    }
    /**
     * Returns all JDAs that are registered in this environment.
     */
    @Override
    public List<JDA> jdas(){
        return shards.stream().map(CommunicatorInterface::jda).collect(Collectors.toList());
    }
    /**
     * @return rank file.
     */
    @Override
    public RankConfiguration rank() {
        return permission;
    }
    /**
     * @param submission the submission the comments are requested from.
     * @return the comments of the submission.
     */
    @Override
    public Optional<List<CommentInterface>> comment(SubmissionInterface submission) {
        return reddit.requestComment(submission.getId());
    }
    /**
     * Attempts to shutdown all communicators.
     * @return the task that will await the shutdown of all communicators.
     */
    @Override
    public Runnable shutdown(){
        log.info("Shutting down the environment.");
        return () -> shards.stream().map(CommunicatorInterface::shutdown).forEach(Runnable::run);
    }
}