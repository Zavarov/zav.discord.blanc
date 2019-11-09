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

package vartas.discord.bot.entities.guild;

import com.google.common.collect.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.utils.cache.UpstreamReference;
import vartas.discord.bot.entities.BotGuild;
import vartas.discord.bot.entities.DiscordCommunicator;
import vartas.discord.bot.entities.DiscordEnvironment;
import vartas.discord.bot.reddit.RedditFeed;
import vartas.discord.bot.reddit.SubredditFeed;
import vartas.discord.bot.visitor.DiscordCommunicatorVisitor;
import vartas.discord.bot.visitor.DiscordEnvironmentVisitor;
import vartas.discord.bot.visitor.guild.SubredditGroupVisitor;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SubredditGroup {
    protected SetMultimap<String, Long> group = HashMultimap.create();
    protected UpstreamReference<Guild> guild;
    protected DiscordEnvironment environment;
    protected DiscordCommunicator communicator;

    public SubredditGroup(Guild guild, DiscordCommunicator communicator){
        this.guild = new UpstreamReference<>(guild);
        this.communicator = communicator;
        this.environment = communicator.environment();
    }

    public synchronized boolean resolve(String key, TextChannel value){
        return group.containsEntry(key, value.getIdLong());
    }

    public synchronized Optional<String> resolve(TextChannel value){
        return group
                .entries()
                .stream()
                .filter(e -> Objects.equals(e.getValue(),value.getIdLong()))
                .map(Map.Entry::getKey)
                .findAny();
    }

    public synchronized Set<TextChannel> resolve(String key){
        return group
                .get(key)
                .stream()
                .map(id -> guild.get().getTextChannelById(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private synchronized Multimap<String, Long> validate(){
        Multimap<String, Long> multimap = HashMultimap.create();

        for(String key : group.keySet()){
            multimap.putAll(key, validate(key));
        }

        return multimap;
    }

    private synchronized Set<Long> validate(String key){
        return Sets.filter(group.get(key), id -> guild.get().getTextChannelById(id) != null);
    }

    public synchronized void remove(String key){
        group.removeAll(key);
        new RemoveSubredditVisitor().accept(key);
        new UpdateGuildVisitor().accept();
    }

    public synchronized void remove(String key, TextChannel value){
        group.remove(key, value.getIdLong());
        new RemoveTextChannelVisitor().accept(key, value);
        new UpdateGuildVisitor().accept();
    }

    public synchronized void add(String key, TextChannel value){
        group.put(key, value.getIdLong());
        new AddTextChannelVisitor().accept(key, value);
        new UpdateGuildVisitor().accept();
    }

    public synchronized void clean(){
        for(String key : group.keySet())
            clean(key);
    }

    public synchronized void clean(String key){
        Set<Long> invalid;

        invalid = validate(key);
        invalid.forEach(value -> group.remove(key, value));
    }

    public synchronized void accept(SubredditGroupVisitor visitor){
        for(String key : group.keySet()){
            clean(key);
            visitor.handleChannels(key, resolve(key));
        }
    }

    @Override
    public synchronized String toString(){
        StringBuilder builder = new StringBuilder();

        //Subreddit Groups
        group.asMap().forEach((key, values) -> {
            builder.append("  subreddit \"").append(key).append("\" {\n");
            values.forEach(value -> {
                builder.append("    channel : ").append(value).append("L\n");
            });
            builder.append("  }\n");
        });

        return builder.toString();
    }

    private class AddTextChannelVisitor implements DiscordEnvironmentVisitor {
        protected String subreddit;
        protected TextChannel channel;

        public void accept(String subreddit, TextChannel channel){
            environment.accept(this);
        }

        @Override
        public void visit(RedditFeed feed){
            if(feed.contains(subreddit))
                feed.add(subreddit);
        }

        @Override
        public void handle(String subreddit, SubredditFeed feed){
            if(this.subreddit.equals(subreddit))
                feed.add(channel);
        }
    }

    private class RemoveSubredditVisitor implements DiscordEnvironmentVisitor{
        protected String subreddit;

        public void accept(String subreddit){
            environment.accept(this);
        }

        @Override
        public void handle(RedditFeed feed){
            feed.remove(subreddit);
        }
    }

    private class RemoveTextChannelVisitor implements DiscordEnvironmentVisitor{
        protected String subreddit;
        protected TextChannel channel;

        public void accept(String subreddit, TextChannel channel){
            this.subreddit = subreddit;
            this.channel = channel;
            environment.accept(this);
        }

        @Override
        public void handle(String subreddit, SubredditFeed feed){
            if(this.subreddit.equals(subreddit))
                feed.remove(channel);
        }
    }

    private class UpdateGuildVisitor implements DiscordCommunicatorVisitor {
        public void accept(){
            communicator.accept(this);
        }

        @Override
        public void handle(BotGuild config){
            if(config.getId().equals(guild.get().getId()))
                config.store();
        }
    }
}
