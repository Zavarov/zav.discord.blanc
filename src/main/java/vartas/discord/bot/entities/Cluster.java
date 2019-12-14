package vartas.discord.bot.entities;

import org.apache.http.client.HttpResponseException;
import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.RedditFeed;
import vartas.discord.bot.StatusTracker;
import vartas.discord.bot.visitor.ClusterVisitor;
import vartas.reddit.Client;
import vartas.reddit.Comment;
import vartas.reddit.Submission;
import vartas.reddit.Subreddit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class Cluster {
    /**
     * The maximum amount of times a Reddit request will be repeated when unsuccessful until we give up.
     */
    private static final int MAX_RETRIES = 7;
    private final ScheduledExecutorService global = Executors.newScheduledThreadPool(2);
    private final Client reddit;
    private final Client pushshift;
    private final RedditFeed feed;
    private final StatusTracker status;
    private final EntityAdapter adapter;

    protected Cluster(Shard shard){
        this.adapter = createEntityAdapter();

        Credentials credentials = adapter.credentials();

        this.reddit = createRedditClient(credentials);
        this.pushshift = createPushshiftClient(credentials);

        this.feed = new RedditFeed(this, shard);
        this.status = new StatusTracker(shard, adapter.status());
    }

    protected abstract EntityAdapter createEntityAdapter();
    protected abstract Client createRedditClient(Credentials credentials);
    protected abstract Client createPushshiftClient(Credentials credentials);

    public void shutdown(){
        global.shutdown();
        reddit.logout();
        pushshift.logout();
    }

    public void accept(ClusterVisitor visitor){
        feed.accept(visitor);
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

}
