package zav.discord.blanc.reddit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.jra.Subreddit;
import zav.jra.observable.SubredditObservable;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class RedditObservable extends SubredditObservable<RedditListener, RedditObserver> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedditObservable.class);

    private final Map<RedditObserver, RedditObserver> identity = new ConcurrentHashMap<>();

    public synchronized RedditObserver get(Subreddit subreddit){
        RedditObserver key = new RedditObserver(subreddit);

        RedditObserver value = identity.getOrDefault(key, null);

        if (value == null) {
            addObserver(key);
            identity.put(key, key);
            return key;
        } else {
            return value;
        }
    }

    @Override
    public synchronized boolean addObserver(RedditObserver observer) {
        identity.put(observer, observer);
        return super.addObserver(observer);
    }

    @Override
    public synchronized boolean removeObserver(RedditObserver observer) {
        identity.remove(observer);
        return super.removeObserver(observer);
    }

    @Override
    public void notifyObserver(RedditObserver observer) throws IOException {
        try {
            observer.notifyAllListener();
        }catch(InvalidObserverException e){
            LOGGER.warn(e.getMessage(), e);
            //SubredditObservable allows concurrent modification of the registered listeners.
            removeObserver(observer);
        }
    }
}
