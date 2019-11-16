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

package vartas.discord.bot.entities;

import com.google.common.collect.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.utils.JDALogger;
import net.dv8tion.jda.internal.utils.cache.UpstreamReference;
import org.slf4j.Logger;
import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.listener.BlacklistListener;
import vartas.discord.bot.listener.CommandListener;
import vartas.discord.bot.reddit.RedditFeed;
import vartas.discord.bot.reddit.SubredditFeed;
import vartas.discord.bot.visitor.DiscordCommunicatorVisitor;
import vartas.discord.bot.visitor.DiscordEnvironmentVisitor;

import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BotGuild {
    public static final String SUBREDDIT = "subreddit";
    public static final String ROLEGROUP = "rolegroup";
    /**
     * The logger for this configuration file.
     */
    protected Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    protected UpstreamReference<Guild> guild;
    protected EntityAdapter adapter;
    protected DiscordCommunicator communicator;

    protected Map<String,SetMultimap<String, Long>> groups = new HashMap<>();
    protected Pattern pattern = null;
    protected String prefix = null;

    public BotGuild(Guild guild, DiscordCommunicator communicator, EntityAdapter adapter){
        this.guild = new UpstreamReference<>(guild);
        this.adapter = adapter;
        this.communicator = communicator;
    }

    public String getId(){
        return guild.get().getId();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Blacklist                                                                                                    //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void set(Pattern blacklist){
        this.pattern = blacklist;

        Optional<Pattern> patternOpt = blacklist();
        if(patternOpt.isPresent())
            new AddBlacklistVisitor().accept(guild.get(), patternOpt.get());
        else
            new RemoveBlacklistVisitor().accept(guild.get());
    }
    public Optional<Pattern> blacklist(){
        return Optional.ofNullable(pattern);
    }
    private class RemoveBlacklistVisitor implements DiscordCommunicatorVisitor {
        protected Guild guild;

        public void accept(Guild guild){
            this.guild = guild;
            communicator.accept(this);
        }

        public void handle(BlacklistListener listener){
            listener.remove(guild);
        }
    }
    private class AddBlacklistVisitor implements DiscordCommunicatorVisitor{
        protected Guild guild;
        protected Pattern pattern;

        public void accept(Guild guild, Pattern pattern){
            this.guild = guild;
            this.pattern = pattern;
            communicator.accept(this);
        }

        public void handle(BlacklistListener listener){
            listener.set(guild, pattern);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Prefix                                                                                                       //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void set(String prefix){
        this.prefix = prefix;

        Optional<String> prefixOpt = prefix();
        if(prefixOpt.isPresent())
            new AddPrefixVisitor().accept(guild.get(), prefixOpt.get());
        else
            new RemovePrefixVisitor().accept(guild.get());
    }
    public Optional<String> prefix(){
        return Optional.ofNullable(prefix);
    }
    private class RemovePrefixVisitor implements DiscordCommunicatorVisitor {
        protected Guild guild;

        public void accept(Guild guild){
            this.guild = guild;
            communicator.accept(this);
        }

        public void handle(CommandListener listener){
            listener.remove(guild);
        }
    }
    private class AddPrefixVisitor implements DiscordCommunicatorVisitor{
        protected Guild guild;
        protected String prefix;

        public void accept(Guild guild, String prefix){
            this.guild = guild;
            this.prefix = prefix;
            communicator.accept(this);
        }

        public void handle(CommandListener listener){
            listener.set(guild, prefix);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Role Group                                                                                                   //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public synchronized boolean resolve(String row, String column, Role value){
        return groups.getOrDefault(row, HashMultimap.create()).get(column).contains(value.getIdLong());
    }
    public synchronized Optional<String> resolve(String row, Role value){
        return Multimaps.filterValues(groups.getOrDefault(row, HashMultimap.create()), l -> Objects.equals(l, value.getIdLong()))
                .keySet()
                .stream()
                .findAny();
    }
    public synchronized void remove(String column, Role value){
        groups.computeIfAbsent(ROLEGROUP, (x) -> HashMultimap.create()).remove(column, value.getIdLong());
    }
    public synchronized void add(String column, Role value){
        groups.computeIfAbsent(ROLEGROUP, (x) -> HashMultimap.create()).put(column, value.getIdLong());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Subreddits                                                                                                   //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public synchronized boolean resolve(String row, String column, TextChannel value){
        return groups.getOrDefault(row, HashMultimap.create()).get(column).contains(value.getIdLong());
    }
    public synchronized Optional<String> resolve(String row, TextChannel value){
        return Multimaps.filterValues(groups.getOrDefault(row, HashMultimap.create()), l -> Objects.equals(l, value.getIdLong()))
                .keySet()
                .stream()
                .findAny();
    }
    public synchronized void remove(String column, TextChannel value){
        groups.computeIfAbsent(SUBREDDIT, (x) -> HashMultimap.create()).remove(column, value.getIdLong());
        new RemoveSubredditChannel().accept(value, column);
    }
    public synchronized void add(String column, TextChannel value){
        groups.computeIfAbsent(SUBREDDIT, (x) -> HashMultimap.create()).put(column, value.getIdLong());
        new AddSubredditChannel().accept(value, column);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   All groups                                                                                                   //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public synchronized void remove(String row, String column){
        groups.getOrDefault(row, HashMultimap.create()).removeAll(column);
    }

    public synchronized <T> Set<T> resolve(String row, String column, BiFunction<Guild, Long, T> mapper){
        clean(row, column, mapper);

        return groups.getOrDefault(row, HashMultimap.create())
                     .get(column)
                     .stream()
                     .map((l) -> mapper.apply(guild.get(), l))
                     .filter(Objects::nonNull)
                     .collect(Collectors.toSet());
    }

    public synchronized <T> Multimap<String, T> resolve(String row, BiFunction<Guild, Long, T> mapper){
        clean(row, mapper);

        Multimap<String, Long> group = groups.getOrDefault(row, HashMultimap.create());
        Multimap<String, T> result = Multimaps.transformValues(group, l -> mapper.apply(guild.get(), l));
        return Multimaps.filterValues(result, Objects::nonNull);
    }

    public synchronized Set<Long> validate(String row, String column, BiFunction<Guild, Long, ?> mapper){
        return Sets.filter(groups.getOrDefault(row, HashMultimap.create()).get(column), l -> mapper.apply(guild.get(), l) == null);
    }

    public synchronized void clean(String row, BiFunction<Guild, Long, ?> mapper){
        for(String key : groups.getOrDefault(row, HashMultimap.create()).keySet())
            clean(row, key, mapper);
    }

    public synchronized void clean(String row, String column, BiFunction<Guild, Long, ?> mapper){
        Set<Long> invalid = validate(row, column, mapper);

        groups.getOrDefault(row, HashMultimap.create()).get(column).removeAll(invalid);
    }

    public synchronized void store(){
        adapter.store(this);
    }

    private class RemoveSubredditChannel implements DiscordEnvironmentVisitor{
        protected TextChannel channel;
        protected String subreddit;

        public void accept(TextChannel channel, String subreddit){
            this.channel = channel;
            this.subreddit = subreddit;
            communicator.environment().accept(this);
        }

        @Override
        public void visit(RedditFeed feed){
            feed.remove(subreddit);
        }

        @Override
        public void handle(String subreddit, SubredditFeed feed){
            if(this.subreddit.equals(subreddit))
                feed.remove(channel);
        }
    }
    private class AddSubredditChannel implements DiscordEnvironmentVisitor{
        protected TextChannel channel;
        protected String subreddit;

        public void accept(TextChannel channel, String subreddit){
            this.channel = channel;
            this.subreddit = subreddit;
            communicator.environment().accept(this);
        }

        @Override
        public void visit(RedditFeed feed){
            feed.add(subreddit);
        }

        @Override
        public void handle(String subreddit, SubredditFeed feed){
            if(this.subreddit.equals(subreddit))
                feed.add(channel);
        }
    }
}