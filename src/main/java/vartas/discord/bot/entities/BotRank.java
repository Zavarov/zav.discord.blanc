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

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.cache.UpstreamReference;
import vartas.discord.bot.EntityAdapter;

import java.util.Optional;

public class BotRank {
    protected LinkedHashMultimap<Long, Type> ranks = LinkedHashMultimap.create();
    protected UpstreamReference<JDA> jda;
    protected EntityAdapter adapter;

    public BotRank(JDA jda, EntityAdapter adapter){
        this.jda = new UpstreamReference<>(jda);
        this.adapter = adapter;
    }

    public synchronized boolean resolve(User key, Type value){
        return ranks.containsEntry(key.getIdLong(), value);
    }

    public synchronized void add(User key, Type value){
        ranks.put(key.getIdLong(), value);
    }

    public synchronized void remove(User key, Type value){
        ranks.remove(key.getIdLong(), value);
    }

    private synchronized Multimap<Long, Type> validate(){
        return Multimaps.filterKeys(ranks, id -> jda.get().getUserById(id) == null);
    }

    public synchronized void clean(){
        Multimap<Long, Type> invalid = validate();
        invalid.forEach((key, value) -> ranks.remove(key, value));
    }

    public synchronized void store(){
        adapter.store(this);
    }

    public synchronized Multimap<User, Type> asMultimap(){
        Multimap<User, Type> result = LinkedHashMultimap.create();

        ranks.asMap().forEach((key, value) -> {
            Optional<User> userOpt = Optional.ofNullable(jda.get().getUserById(key));
            userOpt.ifPresent(user -> result.putAll(user, value));
        });

        return result;
    }

    public enum Type {
        ROOT("Root"),
        REDDIT("Reddit"),
        DEVELOPER("Developer");

        private String name;

        Type(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }
    }
}
