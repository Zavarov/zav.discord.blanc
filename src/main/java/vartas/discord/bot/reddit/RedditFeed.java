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

package vartas.discord.bot.reddit;

import net.dv8tion.jda.internal.utils.JDALogger;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import vartas.discord.bot.entities.DiscordEnvironment;
import vartas.discord.bot.visitor.reddit.RedditFeedVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * This class deals with receiving new submissions from subreddits and posting
 * them in the specified channels.
 */
public class RedditFeed implements Runnable{
    protected Map<String, SubredditFeed> subreddits = new HashMap<>();
    /**
     * The log for this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The runtime of the program.
     */
    protected final DiscordEnvironment environment;
    /**
     * @param environment the runtime of the program.
     */
    public RedditFeed(DiscordEnvironment environment){
        this.environment = environment;
        log.debug("Reddit feeds created.");
    }

    public synchronized void accept(RedditFeedVisitor visitor){
        for(Map.Entry<String,SubredditFeed> entry : subreddits.entrySet())
            visitor.handle(entry.getKey(), entry.getValue());
    }

    public synchronized void add(String subreddit){
        log.debug("Added subreddit '"+subreddit+"'.");
        subreddits.putIfAbsent(subreddit, new SubredditFeed(subreddit, environment));
    }

    public synchronized void remove(String subreddit){
        log.debug("Removed subreddit '"+subreddit+"'.");
        subreddits.remove(subreddit);
    }

    @Override
    public synchronized void run() {
        log.info(String.format("Visiting %d %s.", subreddits.size(), English.plural("subreddit", subreddits.size())));
        subreddits.values().forEach(SubredditFeed::update);
    }
}