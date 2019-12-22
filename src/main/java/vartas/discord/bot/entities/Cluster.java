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
    private final Collection<Shard> shards = new HashSet<>();
    /**
     * The credentials for the different servers and
     */
    private final Credentials credentials;
    /**
     * The internal ranks for all registered users.
     */
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
    @Nonnull
    protected abstract Client createPushshiftClient(@Nonnull Credentials credentials) throws NullPointerException;

    public int getShardId(long guildId){
        return (int)((guildId >> 22) % credentials.getDiscordShards());
    }

    public void shutdown(){
        global.shutdown();
        reddit.logout();
        pushshift.logout();
    }

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

    public void registerShard(@Nonnull Shard shard) throws NullPointerException{
        Preconditions.checkNotNull(shard);
        shards.add(shard);
    }

    public void accept(Visitor visitor){
        visitor.handle(this);
    }

    public abstract static class VisitorDelegator extends Shard.VisitorDelegator implements Visitor {
        private ClusterVisitor clusterVisitor;
        private ShardVisitor shardVisitor;
        private Visitor realThis = this;

        @Override
        public void setRealThis(Visitor realThis){
            this.realThis = realThis;
            if(clusterVisitor != null && clusterVisitor != getRealThis())
                clusterVisitor.setRealThis(realThis);
            if(shardVisitor != null && shardVisitor != getRealThis())
                shardVisitor.setRealThis(realThis);
        }

        @Override
        public Visitor getRealThis(){
            return realThis;
        }

        public void setClusterVisitor(ClusterVisitor clusterVisitor){
            this.clusterVisitor = clusterVisitor;
            this.clusterVisitor.setRealThis(getRealThis());
        }

        public void setShardVisitor(ShardVisitor shardVisitor){
            this.shardVisitor = shardVisitor;
            this.shardVisitor.setRealThis(getRealThis());
        }

        @Override
        public void visit(@Nonnull Cluster cluster){
            if(clusterVisitor != null && clusterVisitor != getRealThis())
                clusterVisitor.visit(cluster);
            if(shardVisitor != null && shardVisitor != getRealThis())
                shardVisitor.visit(cluster);
        }

        @Override
        public void traverse(@Nonnull Cluster cluster){
            if(clusterVisitor != null && clusterVisitor != getRealThis())
                clusterVisitor.traverse(cluster);
            if(shardVisitor != null && shardVisitor != getRealThis())
                shardVisitor.traverse(cluster);
        }

        @Override
        public void endVisit(@Nonnull Cluster cluster){
            if(clusterVisitor != null && clusterVisitor != getRealThis())
                clusterVisitor.endVisit(cluster);
            if(shardVisitor != null && shardVisitor != getRealThis())
                shardVisitor.endVisit(cluster);
        }
    }

    public interface Visitor extends Credentials.Visitor, EntityAdapter.Visitor, Rank.Visitor, Status.Visitor, SubredditFeed.Visitor, Shard.Visitor{
        default void setRealThis(Visitor realThis){
            throw new UnsupportedOperationException();
        }
        default Visitor getRealThis(){
            throw new UnsupportedOperationException();
        }

        default void visit(@Nonnull Cluster cluster){}

        default void traverse(@Nonnull Cluster cluster){}

        default void endVisit(@Nonnull Cluster cluster){}

        default void handle(@Nonnull Cluster cluster) throws NullPointerException{
            Preconditions.checkNotNull(cluster);
            visit(cluster);
            traverse(cluster);
            endVisit(cluster);
        }
    }
    public static class ClusterVisitor implements Visitor{
        private Visitor realThis = this;
        @Override
        public void setRealThis(Visitor realThis){
            this.realThis = realThis;
        }
        @Override
        public Visitor getRealThis(){
            return realThis;
        }
        @Override
        public void traverse(@Nonnull Cluster cluster) {
            cluster.credentials.accept(getRealThis());
            cluster.adapter.accept(getRealThis());
            cluster.rank.accept(getRealThis());
            cluster.status.accept(getRealThis());
            cluster.feed.accept(getRealThis());
        }
    }
    public static class ShardVisitor implements Visitor{
        private Visitor realThis = this;
        @Override
        public void setRealThis(Visitor realThis){
            this.realThis = realThis;
        }
        @Override
        public Visitor getRealThis(){
            return realThis;
        }
        @Override
        public void traverse(@Nonnull Cluster cluster) {
            cluster.shards.forEach(shard -> shard.accept(getRealThis()));
        }
    }
}
