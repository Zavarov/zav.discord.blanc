/*
 * Copyright (C) 2017 u/Zavarov
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

package vartas.discord.bot.api.threads;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.api.environment.EnvironmentInterface;
import vartas.discord.bot.io.guild.GuildConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * This class deals with receiving new submissions from subreddits and posting
 * them in the specified channels.
 */
public class RedditFeed implements Runnable{
    /**
     * The executor that calls this object every minute.
     */
    protected final ScheduledExecutorService executor;
    /**
     * The log for this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The subreddits and their respective feed instance.
     */
    protected final Map<String, SubredditFeed> feeds = new HashMap<>();
    /**
     * The runtime of the program.
     */
    protected final EnvironmentInterface environment;
    /**
     * Only one process at a time is allowed to modify the map.
     */
    protected final Semaphore mutex = new Semaphore(1);
    /**
     * @param environment the runtime of the program.
     */
    public RedditFeed(EnvironmentInterface environment){
        this.environment = environment;
        executor = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder() .setNameFormat("Reddit Feed Executor %d") .build()
        );
        executor.scheduleAtFixedRate(RedditFeed.this, 1, 1, TimeUnit.MINUTES);
        log.info("Reddit feeds created.");
    }
    /**
     * Adds all textchannel in the specified guild that are marked in the configuration file.
     * @param config the configuration file for the guild.
     * @param guild the guild.
     */
    public void addSubreddits(GuildConfiguration config, Guild guild){
        mutex.acquireUninterruptibly();

        config.getRedditFeeds(guild).entries().forEach( entry -> {
            String subreddit = entry.getKey();
            TextChannel channel = entry.getValue();

            feeds.computeIfAbsent(subreddit, x -> new SubredditFeed(x, environment)).addTextChannel(channel);
            log.info(String.format("Added the subreddit %s for the guild %s.",subreddit,guild.getName()));
        });
        
        mutex.release();
    }
    /**
     * Adds a new textchannel to the list of feeds. New submission will be posted there.
     * @param subreddit the subreddit.
     * @param channel the textchannel.
     */
    public void addFeed(String subreddit, TextChannel channel){
        mutex.acquireUninterruptibly();

        feeds.computeIfAbsent(subreddit, (s) -> new SubredditFeed(s, environment)).addTextChannel(channel);
        
        mutex.release();
    }
    /**
     * Removes a textchannel from the list of feeds. New submissions won't be posted there anymore.
     * @param subreddit the subreddit.
     * @param channel the textchannel.
     */
    public void removeFeed(String subreddit, TextChannel channel){
        mutex.acquireUninterruptibly();

        if(feeds.containsKey(subreddit)) {
            feeds.get(subreddit).removeTextChannel(channel);
            //Remove the feed if this was the last text channel.
            if(feeds.get(subreddit).getTextChannels().isEmpty())
                feeds.remove(subreddit);
        }

        mutex.release();
    }
    /**
     * @param subreddit a subreddit.
     * @param channel a textchannel.
     * @return true if the (subreddit, channel) pair can receive new submissions.
     */
    public boolean containsFeed(String subreddit, TextChannel channel){
        mutex.acquireUninterruptibly();

        boolean result = false;

        if(feeds.containsKey(subreddit))
            result = feeds.get(subreddit).getTextChannels().contains(channel);

        mutex.release();

        return result;
    }

    @Override
    public void run() {
        mutex.acquireUninterruptibly();

        feeds.values().forEach(SubredditFeed::update);

        mutex.release();
    }
}