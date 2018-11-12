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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import vartas.discordbot.threads.RedditFeed;
import vartas.discordbot.threads.StatusTracker;
import vartas.parser.cfg.ContextFreeGrammar;
import vartas.reddit.PushshiftWrapper;
import vartas.reddit.PushshiftWrapper.CompactComment;
import vartas.reddit.PushshiftWrapper.CompactSubmission;
import vartas.reddit.RedditBot;
import vartas.reddit.SubmissionWrapper;
import vartas.reddit.SubredditWrapper;
import vartas.xml.XMLCommand;
import vartas.xml.XMLConfig;
import vartas.xml.XMLCredentials;
import vartas.xml.XMLPermission;
import vartas.xml.strings.XMLStringList;

/**
 * This class implements the communication with the Reddit API and the crawler.<br>
 * Additionally, it also implements the XML files, with the exception of the
 * config file.
 * @author u/Zavarov
 */
public abstract class AbstractEnvironment implements Environment{
    /**
     * All bots indexed by their shard.
     */
    protected List<Communicator> shards;
    /**
     * The instance that is used to crawl through the Reddit submissions.
     */
    protected PushshiftWrapper pushshift;
    /**
     * The instance that communicates with the Reddit API.
     */
    protected RedditBot reddit;
    /**
     * The feed that checks for new submissions.
     */
    protected RedditFeed feed;
    /**
     * The file containing all the login data.
     */
    protected XMLCredentials credentials;
    /**
     * The list of users with special ranks.
     */
    protected XMLPermission permission;
    /**
     * All status messages.
     */
    protected XMLStringList status;
    /**
     * The list of all commands and their respective classes.
     */
    protected XMLCommand command;
    /**
     * The configuration file.
     */
    protected XMLConfig config;
    /**
     * The CFG used by the parser.
     */
    protected ContextFreeGrammar grammar;
    /**
     * The tracker that updates the game.
     */
    protected StatusTracker tracker;
    /**
     * The underlying network adapter for the Reddit API calls.
     */
    protected NetworkAdapter adapter;
    /**
     * Loads the remaining XML files and initializes the Reddit communicators. 
     * @param config the configuration file.
     * @param adapter the network adapter for the Reddit API calls.
     */
    protected AbstractEnvironment(XMLConfig config, NetworkAdapter adapter){
        this.adapter = adapter;
        this.config = config;
        this.credentials = XMLCredentials.create(new File(String.format("%s/credentials.xml", config.getDataFolder())));
        this.permission = XMLPermission.create(new File(String.format("%s/permission.xml", config.getDataFolder())));
        this.status = XMLStringList.create(new File(String.format("%s/status.xml", config.getDataFolder())));
        this.command = XMLCommand.create(new File(String.format("%s/command.xml", config.getDataFolder())));
        this.grammar = new ContextFreeGrammar.Builder(new File(String.format("%s/grammar.xml", config.getDataFolder()))).build();
        
        this.shards = new ObjectArrayList<>(config.getDiscordShards());
        this.reddit = new RedditBot(credentials, adapter);
        this.feed = new RedditFeed(this);
        this.pushshift = new PushshiftWrapper(reddit);
        this.pushshift.read();
        
        this.tracker = new StatusTracker(this);
    }
    /**
     * @return config file.
     */
    @Override
    public XMLConfig config(){
        return config;
    }
    /**
     * @return credentials file.
     */
    @Override
    public XMLCredentials credentials(){
        return credentials;
    }
    /**
     * @return permission file.
     */
    @Override
    public XMLPermission permission(){
        return permission;
    }
    /**
     * @return the list of all status messages. 
     */
    @Override
    public XMLStringList status(){
        return status;
    }
    /**
     * @return the file containing all valid commands. 
     */
    @Override
    public XMLCommand command(){
        return command;
    }
    /**
     * @return the CFG of the parser
     */
    @Override
    public ContextFreeGrammar grammar(){
        return grammar;
    }
    /**
     * @param subreddit the subreddit the submissions are from.
     * @param start the inclusively oldest submission in the interval.
     * @param end the inclusively newest submission in the interval.
     * @return the submissions in the subreddit within the given interval.
     */
    @Override
    public List<Submission> submission(String subreddit, Instant start, Instant end){
        SubmissionWrapper wrapper = new SubmissionWrapper(reddit);
        wrapper.parameter(subreddit, Date.from(start), Date.from(end));
        return wrapper.request();
    }
    /**
     * @param subreddit the name of the subreddit.
     * @return the subreddit instance with that name.
     */
    @Override
    public Subreddit subreddit(String subreddit){
        SubredditWrapper wrapper = new SubredditWrapper(reddit);
        wrapper.parameter(subreddit);
        return wrapper.request();
    }
    /**
     * @param subreddit the subreddit the submissions are from
     * @return a map containing all requested submissions keyed by their submission date.
     */
    @Override
    public ListMultimap<Instant, CompactSubmission> compactSubmission(String subreddit){
        return pushshift.getSubmissions(subreddit);
    }
    /**
     * @param date the date the submissions were submitted.
     * @return a map containing all requested submissions keyed by their subreddit.
     */
    @Override
    public ListMultimap<String, CompactSubmission> compactSubmission(Instant date){
        return pushshift.getSubmissions(date);
    }
    /**
     * @param subreddit the subreddit the submissions are from
     * @return a map containing all requested comments keyed by their comment date.
     */
    @Override
    public ListMultimap<Instant, CompactComment> compactComment(String subreddit){
        return pushshift.getComments(subreddit);
    }
    /**
     * @param date the date the submissions were submitted.
     * @return a map containing all requested comments keyed by their subreddit.
     */
    @Override
    public ListMultimap<String, CompactComment> compactComment(Instant date){
        return pushshift.getComments(date);
    }
    /**
     * @return all guilds distributed over all shards.
     */
    @Override
    public Collection<Guild> guild(){
        return shards.stream()
                .flatMap(b -> b.guild().stream())
                .collect(Collectors.toList());
    }
    /**
     * @param id the id of the guild.
     * @return the bot that this guild belongs to. 
     */
    @Override
    public Communicator comm(long id){
        int shard = (int)((id >> 22) % config().getDiscordShards());
        return shards.get(shard);
    }
    /**
     * Terminates the threads in all shards.
     */
    @Override
    public void shutdown(){
        feed.shutdown();
        tracker.shutdown();
        shards.forEach(Communicator::shutdown);
    }
    /**
     * @return the game that is currently being played by the instance. 
     */
    @Override
    public Game game(){
        return shards.stream()
                .map(b -> b.presence().getGame())
                .filter(g -> g != null)
                .findAny()
                .orElse(null);
    }
    /**
     * Sets the new game of the instance.
     * @param game the new game that is played.
     */
    @Override
    public void game(Game game){
        shards.forEach(b -> b.presence().setGame(game));
    }
    /**
     * @return the online status of the current bot instance.
     */
    @Override
    public OnlineStatus onlinestatus(){
        return shards.stream()
                .map(b -> b.presence().getStatus()).findFirst()
                .orElse(OnlineStatus.UNKNOWN);
    }
    /**
     * Sets the bot status of the current instance to the new status.
     * @param status 
     */
    @Override
    public void onlinestatus(OnlineStatus status){
        shards.stream()
                .map(b -> b.presence())
                .forEach(p -> p.setStatus(status));
    }
    /**
     * @return the network adapter that is used for the Reddit requests.
     */
    @Override
    public NetworkAdapter adapter(){
        return adapter;
    }
    /**
     * Makes the program post submissions from the subreddit in the specified channel.
     * @param subreddit the name of the subreddit.
     * @param channel the textchannel where new submissions are posted.
     */
    @Override
    public void add(String subreddit, TextChannel channel){
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
        feed.removeFeed(subreddit, channel);
    }
    /**
     * Requests the submissions and comments in the subreddit within the
     * given interval via the pushshift crawler.
     * @param subreddit the name of the 
     * @param start the inclusive time stamp of the oldest submission.
     * @param end the inclusive time stamp of the newest submission.
     * @throws IOException when the HTTP request failed
     */
    @Override
    public synchronized void request(String subreddit, Instant start, Instant end) throws IOException{
        pushshift.parameter(subreddit, end, start);
        pushshift.request();
    }
    /**
     * Writes the current content of the crawler to the hard disk.
     * @throws IOException if an error occured while writing the data.
     * @throws InterruptedException if the program was interrupted before the writing process was finished.
     */
    @Override
    public void store() throws IOException, InterruptedException{
        pushshift.store();
    }
}