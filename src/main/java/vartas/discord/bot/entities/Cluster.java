package vartas.discord.bot.entities;

import com.google.common.base.Preconditions;
import org.apache.http.client.HttpResponseException;
import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.JSONEntityAdapter;
import vartas.discord.bot.SubredditFeed;
import vartas.discord.bot.internal.UpdateStatusMessage;
import vartas.reddit.Client;
import vartas.reddit.Comment;
import vartas.reddit.Submission;
import vartas.reddit.Subreddit;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The cluster is the overarching structure managing all shards. It is responsible for all tasks that should
 * only be executed by a single task. Among those things are updating the status message or communicating with
 * the Reddit API.<br>
 * All shards will have a reference to a local cluster, but for everyone but the master shard,
 * this will be {@code null}.
 */
@Nonnull
public abstract class Cluster {
    /**
     * The maximum amount of times a Reddit request will be repeated when unsuccessful until we give up.
     */
    private static final int MAX_RETRIES = 7;
    /**
     * The executor for all global task. Since we only have two global tasks, the will keep exactly that
     * many active.
     */
    @Nonnull
    private final ScheduledExecutorService global = Executors.newScheduledThreadPool(2);
    /**
     * The interface for communicating with the Reddit API.
     */
    @Nonnull
    private final Client reddit;
    /**
     * The interface for communicating with the Pushshift API.
     */
    @Nonnull
    private final Client pushshift;
    /**
     * The thread for requesting and posting the latest subreddit submissions.
     */
    @Nonnull
    private final SubredditFeed feed;
    /**
     * All possible status messages.
     */
    @Nonnull
    private final Status status;
    /**
     * The adapter for reading and writing to the configuration files.
     */
    @Nonnull
    private final EntityAdapter adapter;
    /**
     * All shards of this cluster.
     */
    @Nonnull
    private final Collection<Shard> shards = new HashSet<>();
    /**
     * The credentials for the different servers and
     */
    @Nonnull
    private final Credentials credentials;
    /**
     * The internal ranks for all registered users.
     */
    @Nonnull
    private final Rank rank;

    /**
     * Initializes an empty cluster over the specified shard.
     * Note that it will not connect to the Reddit API. This has to be done in {@link #createRedditClient(Credentials)}
     * and {@link #createPushshiftClient(Credentials)},  respectively.
     */
    protected Cluster() {
        this.adapter = createEntityAdapter();
        this.rank = adapter.rank();
        this.credentials = adapter.credentials();
        this.reddit = createRedditClient(credentials);
        this.pushshift = createPushshiftClient(credentials);
        this.feed = new SubredditFeed(this);
        this.status = adapter.status();

        global.scheduleAtFixedRate(() -> this.accept(new UpdateStatusMessage()), 0, credentials.getStatusMessageUpdateInterval(), TimeUnit.MINUTES);
        global.scheduleAtFixedRate(feed, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Creates a fresh adapter for reading and writing from the internal configuration files.
     * Since there is no adapter specified, it is up to the user to either use the {@link JSONEntityAdapter} or
     * implement their own.
     * @return an adapter for accessing the configuration files
     */
    @Nonnull
    protected abstract EntityAdapter createEntityAdapter();

    /**
     * Creates a new client for communicating with Reddit. This client already has to be logged in, since
     * we won't do this later on.
     * @param credentials the credentials containing the information for logging in
     * @return the interface for communicating with the Reddit API
     * @throws NullPointerException if {@code credentials} is null
     */
    @Nonnull
    protected abstract Client createRedditClient(@Nonnull Credentials credentials) throws NullPointerException;

    /**
     * Creates a new client for communicating with the Pushshift API
     * @param credentials the credentials containing the information for logging in
     * @return the interface for communicating with the Pushshift API
     * @throws NullPointerException if {@code credentials} is null
     */
    @Nonnull
    protected abstract Client createPushshiftClient(@Nonnull Credentials credentials) throws NullPointerException;

    /**
     * The formula for getting the shard for the respective guild id is <b>id >> 22 mod #shards</b>
     * {@see  <a href="https://discordapp.com/developers/docs/topics/gateway">https://discordapp.com/developers/docs/topics/gateway</a>}
     * @param guildId the guild id
     * @return the shard the guild with the respective id is in
     */
    public int getShardId(long guildId){
        return (int)((guildId >> 22) % credentials.getDiscordShards());
    }

    /**
     * Terminates all running tasks and cuts all connections to the connected servers.<br>
     * The program has to be restarted after using this command.
     */
    public void shutdown(){
        global.shutdown();
        reddit.logout();
        pushshift.logout();
    }
    @Nonnull
    public Optional<Subreddit> subreddit(@Nonnull String subreddit) throws IllegalArgumentException{
        try {
            return reddit.requestSubreddit(subreddit, MAX_RETRIES);
        }catch(HttpResponseException e){
            throw new IllegalArgumentException(e);
        }
    }
    @Nonnull
    public Optional<? extends Collection<Comment>> comment(@Nonnull Submission submission) throws IllegalArgumentException{
        try {
            return reddit.requestComment(submission.getId(), MAX_RETRIES);
        }catch(HttpResponseException e){
            throw new IllegalArgumentException(e);
        }
    }
    @Nonnull
    public Optional<? extends Collection<Submission>> pushshift(@Nonnull String subreddit, @Nonnull LocalDateTime start, @Nonnull LocalDateTime end) throws IllegalArgumentException{
        try {
            return pushshift.requestSubmission(subreddit, start, end, MAX_RETRIES);
        }catch(HttpResponseException e){
            throw new IllegalArgumentException(e);
        }
    }
    @Nonnull
    public Optional<? extends Collection<Submission>> submission(@Nonnull String subreddit, @Nonnull LocalDateTime start, @Nonnull LocalDateTime end) throws IllegalArgumentException{
        try {
            return reddit.requestSubmission(subreddit, start, end, MAX_RETRIES);
        }catch(HttpResponseException e){
            throw new IllegalArgumentException(e);
        }
    }

    public void registerShard(@Nonnull Shard shard) {
        shards.add(shard);
    }

    public void accept(@Nonnull Visitor visitor){
        visitor.handle(this);
    }

    public interface Visitor extends Credentials.Visitor, EntityAdapter.Visitor, Rank.Visitor, Status.Visitor, SubredditFeed.Visitor, Shard.Visitor{
        default void visit(@Nonnull Cluster cluster){}
        default void endVisit(@Nonnull Cluster cluster){}

        default void traverse(@Nonnull Cluster cluster){
            cluster.credentials.accept(this);
            cluster.adapter.accept(this);
            cluster.rank.accept(this);
            cluster.status.accept(this);
            cluster.feed.accept(this);
            cluster.shards.forEach(shard -> shard.accept(this));
        }

        default void handle(@Nonnull Cluster cluster) throws NullPointerException{
            Preconditions.checkNotNull(cluster);
            visit(cluster);
            traverse(cluster);
            endVisit(cluster);
        }
    }
}
