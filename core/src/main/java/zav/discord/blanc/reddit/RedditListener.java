package zav.discord.blanc.reddit;

import zav.jra.Subreddit;
import zav.jra.listener.SubredditListener;

public interface RedditListener extends SubredditListener {
    default void destroy(Subreddit subreddit){
        destroy(subreddit.getName());
    }

    void destroy(String name);
}
