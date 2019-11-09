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
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.utils.cache.UpstreamReference;
import vartas.discord.bot.entities.BotGuild;
import vartas.discord.bot.entities.DiscordCommunicator;
import vartas.discord.bot.visitor.DiscordCommunicatorVisitor;
import vartas.discord.bot.visitor.guild.RoleGroupVisitor;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleGroup {
    protected SetMultimap<String, Long> group = HashMultimap.create();
    protected DiscordCommunicator communicator;
    protected UpstreamReference<Guild> guild;

    public RoleGroup(Guild guild, DiscordCommunicator communicator){
        this.guild = new UpstreamReference<>(guild);
        this.communicator = communicator;
    }

    public synchronized boolean resolve(String key, Role value){
        return group.containsEntry(key, value.getIdLong());
    }

    public synchronized Optional<String> resolve(Role value){
        return group
                .entries()
                .stream()
                .filter(e -> Objects.equals(e.getValue(),value.getIdLong()))
                .map(Map.Entry::getKey)
                .findAny();
    }

    public synchronized Set<Role> resolve(String key){
        return group
                .get(key)
                .stream()
                .map(id -> guild.get().getRoleById(id))
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
        return Sets.filter(group.get(key), id -> guild.get().getRoleById(id) != null);
    }

    public synchronized void remove(String key){
        group.removeAll(key);
        new UpdateGuildVisitor().accept();
    }

    public synchronized void remove(String key, Role value){
        group.remove(key, value.getIdLong());
        new UpdateGuildVisitor().accept();
    }

    public synchronized void add(String key, Role value){
        group.put(key, value.getIdLong());
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

    public synchronized void accept(RoleGroupVisitor visitor){
        for(String key : group.keySet()){
            clean(key);
            visitor.handleRoles(key, resolve(key));
        }
    }

    @Override
    public synchronized String toString(){
        StringBuilder builder = new StringBuilder();

        //Role Groups
        group.asMap().forEach((key, values) -> {
            builder.append("  rolegroup \"").append(key).append("\" {\n");
            values.forEach(value -> {
                builder.append("    role : ").append(value).append("L\n");
            });
            builder.append("  }\n");
        });

        return builder.toString();
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
