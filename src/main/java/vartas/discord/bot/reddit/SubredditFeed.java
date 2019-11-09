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

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import vartas.discord.bot.entities.DiscordEnvironment;
import vartas.discord.bot.entities.guild.SubredditGroup;
import vartas.discord.bot.visitor.DiscordEnvironmentVisitor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubredditFeed {
    /**
     * The log for this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The global environment.
     */
    protected DiscordEnvironment environment;
    /**
     * The subreddit that is covered by this feed.
     */
    protected String subreddit;
    /**
     * The underlying cache of previously received submissions.
     */
    protected SubmissionCache cache;
    /**
     * All text channels new submissions are posted in.<br>
     * In order to avoid looking through all configuration files during each iteration,
     * the set should be a life view of all registered channels.<br>
     * Meaning that every time the configuration files are updated, the change should
     * be registered in this feed as well.
     */
    protected Set<TextChannel> channels = new HashSet<>();

    public SubredditFeed(String subreddit, DiscordEnvironment environment){
        this.subreddit = subreddit;
        this.environment = environment;
        this.cache = new SubmissionCache(subreddit, environment);
    }

    public synchronized void add(TextChannel channel){
        channels.add(channel);
    }

    public synchronized void remove(TextChannel channel){
        channels.remove(channel);
        //Remove this entire feed if no more channels remain
        if(channels.isEmpty())
            new RemoveSubredditVisitor().accept();
    }

    public synchronized void update(){
        try{
            List<MessageBuilder> submissions = receive();

            send(submissions);

            if(submissions.size() > 0)
                log.info(String.format("Posted %d new %s from r/%s", submissions.size(), English.plural("submission", submissions.size()),subreddit));
            //"If any execution of the task encounters an exception, subsequent executions are suppressed" my ass
        }catch(Exception t){
            log.error("Unhandled exception caught from "+subreddit,t);
        }
    }

    private List<MessageBuilder> receive(){
        //Go back 2:30 minutes so that we overlap with the previous run by 30 seconds
        Instant start = Instant.now().minus(150, ChronoUnit.SECONDS);
        //Submissions should be at least 1 minute old so that the author can flair them correctly
        Instant end = Instant.now().minus(60, ChronoUnit.SECONDS);

        cache.request(start, end);
        return cache.retrieve(start, end);
    }

    private void send(List<MessageBuilder> messages){
        for(MessageBuilder message : messages)
            send(messages);
    }

    private void send(MessageBuilder message){
        for(TextChannel channel : channels)
            send(message, channel);
    }

    private void send(MessageBuilder messages, TextChannel channel){
        //Handle Discord exceptions
        try {
            environment.communicator(channel).send(channel, messages);
            //Impossible to send in this channel
        }catch(InsufficientPermissionException e){
            log.warn("Couldn't send a submission from "+subreddit, e.getMessage());
            new RemoveTextChannelVisitor().accept(channel);
            //#TODO Ignore Discord being unavailable
        }catch(ErrorResponseException e){
            log.warn("Couldn't send a submission from "+subreddit, e.getMessage());
            ErrorResponse response = e.getErrorResponse();
            if(response == ErrorResponse.UNKNOWN_GUILD || response == ErrorResponse.UNKNOWN_CHANNEL) {
                new RemoveTextChannelVisitor().accept(channel);
            }
        }
    }

    private class RemoveTextChannelVisitor implements DiscordEnvironmentVisitor{
        protected TextChannel channel;
        public void accept(TextChannel channel){
            this.channel = channel;
            environment.accept(this);
        }
        @Override
        public void handle(SubredditGroup group){
            environment.schedule(() -> group.remove(subreddit, channel));
        }
    }

    private class RemoveSubredditVisitor implements DiscordEnvironmentVisitor {
        public void accept(){
            environment.accept(this);
        }
        @Override
        public void handle(SubredditGroup group){
            environment.schedule(() -> group.remove(subreddit));
        }
    }
}
