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
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import vartas.discordbot.threads.Killable;
import vartas.parser.cfg.ContextFreeGrammar;
import vartas.reddit.PushshiftWrapper.CompactComment;
import vartas.reddit.PushshiftWrapper.CompactSubmission;
import vartas.xml.XMLCommand;
import vartas.xml.XMLConfig;
import vartas.xml.XMLCredentials;
import vartas.xml.XMLPermission;
import vartas.xml.strings.XMLStringList;

/**
 * This interface is intended to be used as a tool to deal with the communicators
 * for each shard.
 * @author u/Zavarov
 */
public interface Environment extends Killable{
    /**
     * @return the configuration file.
     */
    public abstract XMLConfig config();
    /**
     * @return credentials file.
     */
    public abstract XMLCredentials credentials();
    /**
     * @return permission file.
     */
    public abstract XMLPermission permission();
    /**
     * @return the list of all status messages. 
     */
    public abstract XMLStringList status();
    /**
     * @return the file containing all valid commands. 
     */
    public abstract XMLCommand command();
    /**
     * @return the CFG of the parser
     */
    public abstract ContextFreeGrammar grammar();
    /**
     * @param subreddit the subreddit the submissions are from.
     * @param start the inclusively oldest submission in the interval.
     * @param end the inclusively newest submission in the interval.
     * @return the submissions in the subreddit within the given interval.
     */
    public abstract List<Submission> submission(String subreddit, Instant start, Instant end);
    /**
     * @param subreddit the name of the subreddit.
     * @return the subreddit instance with that name.
     */
    public abstract Subreddit subreddit(String subreddit);
    /**
     * @param subreddit the subreddit the submissions are from
     * @return a map containing all requested submissions keyed by their submission date.
     */
    public abstract ListMultimap<Instant, CompactSubmission> compactSubmission(String subreddit);
    /**
     * @param date the date the submissions were submitted.
     * @return a map containing all requested submissions keyed by their subreddit.
     */
    public abstract ListMultimap<String, CompactSubmission> compactSubmission(Instant date);
    /**
     * @param subreddit the subreddit the submissions are from.
     * @param date the date the submissions were submitted.
     * @return a list of all submissions from that subreddit on that specific date.
     */
    public default List<CompactSubmission> compactSubmission(String subreddit, Instant date){
        return compactSubmission(subreddit).get(date);
    }
    /**
     * @param subreddit the subreddit the submissions are from.
     * @param start the inclusively oldest submission in the interval.
     * @param end the inclusively newest submission in the interval.
     * @return a list of all submissions from that subreddit within that specified interval.
     */
    public default List<CompactSubmission> compactSubmission(String subreddit, Instant start, Instant end){
        return compactSubmission(subreddit)
                .entries().stream()
                .filter(e -> !e.getKey().isBefore(start) && !e.getKey().isAfter(end))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
    /**
     * @param subreddit the subreddit the submissions are from
     * @return a map containing all requested comments keyed by their comment date.
     */
    public abstract ListMultimap<Instant, CompactComment> compactComment(String subreddit);
    /**
     * @param date the date the submissions were submitted.
     * @return a map containing all requested comments keyed by their subreddit.
     */
    public abstract ListMultimap<String, CompactComment> compactComment(Instant date);
    /**
     * @param subreddit the subreddit the comments are from.
     * @param date the date the comments were submitted.
     * @return a list of all comments from that subreddit on that specific date.
     */
    public default List<CompactComment> compactComment(String subreddit, Instant date){
        return compactComment(subreddit).get(date);
    }
    /**
     * @param subreddit the subreddit the comments are from.
     * @param start the inclusively oldest comment in the interval.
     * @param end the inclusively newest comment in the interval.
     * @return a list of all comments from that subreddit within that specified interval.
     */
    public default List<CompactComment> compactComment(String subreddit, Instant start, Instant end){
        return compactComment(subreddit)
                .entries().stream()
                .filter(e -> !e.getKey().isBefore(start) && !e.getKey().isAfter(end))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
    /**
     * @return the network adapter that is used for the Reddit requests.
     */
    public abstract NetworkAdapter adapter();
    /**
     * @return all guilds distributed over all shards.. 
     */
    public abstract Collection<Guild> guild();
    /**
     * @param guild a guild instance.
     * @return the bot that this guild belongs to. 
     */
    public default Communicator comm(Guild guild){
        return comm(guild.getId());
    }
    /**
     * @param channel a text channel inside a guild.
     * @return the bot that this guild belongs to. 
     */
    public default Communicator comm(TextChannel channel){
        return comm(channel.getId());
    }
    /**
     * @param role a role inside a guild.
     * @return the bot that this guild belongs to. 
     */
    public default Communicator comm(Role role){
        return comm(role.getId());
    }
    /**
     * @param id the id of the guild.
     * @return the bot that this guild belongs to. 
     * @throws NumberFormatException if the string can't be parsed as a long.
     */
    public default Communicator comm(String id) throws NumberFormatException{
        return comm(Long.parseLong(id));
    }
    /**
     * @param id the id of the guild.
     * @return the bot that this guild belongs to. 
     */
    public abstract Communicator comm(long id);
    /**
     * The tracker that updates the game.
     */
    /**
     * @return the game that is currently being played by the instance. 
     */
    public abstract Game game();
    /**
     * Sets the new game of the instance.
     * @param game the new game that is played.
     */
    public abstract void game(Game game);
    /**
     * @return the online status of the current bot instance.
     */
    public abstract OnlineStatus onlinestatus();
    /**
     * Sets the bot status of the current instance to the new status.
     * @param status 
     */
    public abstract void onlinestatus(OnlineStatus status);
}